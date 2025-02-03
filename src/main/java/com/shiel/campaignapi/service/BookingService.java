package com.shiel.campaignapi.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.shiel.campaignapi.dto.BookingDto;
import com.shiel.campaignapi.dto.DependentDto;
import com.shiel.campaignapi.dto.EventDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Dependent;
import com.shiel.campaignapi.entity.Dependent.Relation;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.exception.UserIllegalArgumentException;
import com.shiel.campaignapi.repository.BookingRepository;
import com.shiel.campaignapi.repository.DependentRepository;
import com.shiel.campaignapi.repository.EventRepository;
import com.shiel.campaignapi.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	@Autowired
	EventRepository eventRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DependentRepository dependentRepository;

	@Autowired
	private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

	public BookingService(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;

	}

	@Transactional
	public Booking saveBooking(BookingDto bookingDto) {
		try {
			logger.info("Starting booking process for event ID: {}", bookingDto.getEventId());

			Event event = eventRepository.findById(bookingDto.getEventId().toString())
					.orElseThrow(() -> new RuntimeException("Event not found with ID: " + bookingDto.getEventId()));

			logger.debug("Event found: {}", event.getTitle());

			String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			User currentUser = userRepository.findByEmail(currentUserEmail)
					.orElseThrow(() -> new RuntimeException("Current user not found with email: " + currentUserEmail));

			logger.debug("Current user found: {}", currentUser.getEmail());

			int newDependentsCount = (bookingDto.getDependents() != null) ? bookingDto.getDependents().size() : 0;

			logger.debug("New dependents count: {}", newDependentsCount);

			int availableSeats = event.getSeats() - event.getSeatsBooked();
			if (availableSeats <= 0 || newDependentsCount > availableSeats) {

				logger.error("Booking is full for this event. No more seats available.");

				throw new UserIllegalArgumentException("Booking is full", "No more seats available", 500);
			}

			Booking booking = bookingRepository.findByUserIdAndEventId(currentUser, event).orElse(null);
			boolean isNewBooking = (booking == null);
			boolean isUserBookingForSelf = bookingDto.isBookingForSelf();

			logger.debug("Is this a new booking: {}", isNewBooking);

			BigDecimal totalAmount = BigDecimal.ZERO;

			if (isNewBooking) {
				// totalAmount = totalAmount.add(event.getAdultAmount());
				booking = new Booking(); 
				booking.setPaymentVia(bookingDto.getPaymentVia());
				booking.setAmountPaid(bookingDto.getAmountPaid());
				booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
				booking.setIsPaid(bookingDto.getIsPaid());
				booking.setBookingDate(LocalDateTime.now());
				booking.setUserId(currentUser);
				booking.setEventId(event);

				logger.info("Created new booking for user: {}", currentUser.getEmail());
			}
			if (isUserBookingForSelf) {
				totalAmount = totalAmount.add(event.getAdultAmount());
				booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
			} else {
				booking.setBookingStatus(Booking.BookingStatus.DEPENDENT);
			}

			List<Dependent> newDependents = new ArrayList<>();
			if (bookingDto.getDependents() != null && !bookingDto.getDependents().isEmpty()) {
				for (DependentDto dependentDto : bookingDto.getDependents()) {
					BigDecimal amount = BigDecimal.ZERO;

					if (dependentDto.getAge() >= 7 && dependentDto.getAge() <= 12) {
						// totalAmount = totalAmount.add(event.getChildAmount());
						amount = event.getChildAmount();

					} else if (dependentDto.getAge() > 12) {
						// totalAmount = totalAmount.add(event.getAdultAmount());
						amount = event.getAdultAmount();
					}
					if (dependentDto.getAge() < 7) {
						amount = BigDecimal.ZERO;
					}

					totalAmount = totalAmount.add(amount);
				}
					List<Dependent> existingDependents = booking.getDependents() != null ? booking.getDependents()
							: new ArrayList<>();

					boolean hasHusband = existingDependents.stream().anyMatch(d -> d.getRelation() == Relation.HUSBAND);
					boolean hasWife = existingDependents.stream().anyMatch(d -> d.getRelation() == Relation.WIFE);

					for (DependentDto dependentDto : bookingDto.getDependents()) {

						String gender = dependentDto.getGender();
						Relation relation = dependentDto.getRelation();

						if ("Male".equalsIgnoreCase(gender)) {
							if (!Set.of(Relation.SON, Relation.HUSBAND, Relation.BROTHER, Relation.FATHER)
									.contains(relation)) {
								throw new UserIllegalArgumentException(
										" Allowed values are: Son, Husband, Brother, Father",
										"Invalid relation for male gender", 500);
							}
							if (relation == Relation.HUSBAND && hasHusband) {
								throw new UserIllegalArgumentException("Only one husband can be added",
										"Duplicate Husband", 500);
							}
							if (relation == Relation.HUSBAND && dependentDto.getAge() < 21) {
								throw new UserIllegalArgumentException("Husband age must be atleast 21",
										"Verify Husband's Age", 500);
							}
							if (relation == Relation.FATHER && dependentDto.getAge() <= currentUser.getAge()) {
								throw new UserIllegalArgumentException(
										"Father's age must be greater than the user's age", "Verify Father's Age", 500);
							}

						} else if ("Female".equalsIgnoreCase(gender)) {

							if (!Set.of(Relation.WIFE, Relation.MOTHER, Relation.DAUGHTER, Relation.SISTER)
									.contains(relation)) {
								throw new UserIllegalArgumentException(" Allowed values are: Wife, Mother, Daughter",
										"Invalid relation for female gender", 400);
							}
							if (relation == Relation.WIFE && dependentDto.getAge() < 18) {
								throw new UserIllegalArgumentException("Wife age must be at least 18",
										" Verify Wife's Age", 400);
							}
							if (relation == Relation.MOTHER && dependentDto.getAge() <= currentUser.getAge()) {
								throw new UserIllegalArgumentException(
										"Mother's age must be greater than the user's age", " Verify Mother's Age",
										400);
							}
							if (relation == Relation.WIFE && hasWife) {
								throw new UserIllegalArgumentException("Only one wife can be added", "Duplicate Wife",
										500);
							}
						}

						Dependent dependent = new Dependent();
						dependent.setName(dependentDto.getName());
						dependent.setPlace(dependentDto.getPlace());
						dependent.setGender(dependentDto.getGender());
						dependent.setAge(dependentDto.getAge());
						dependent.setRelation(dependentDto.getRelation());
						dependent.setUserId(currentUser);
						dependent.setBookingId(booking);
						newDependents.add(dependent);

						if (relation == Relation.HUSBAND) {
							hasHusband = true;
						}
						if (relation == Relation.WIFE) {
							hasWife = true;
						}
					}
					dependentRepository.saveAll(newDependents);

					if (booking.getDependents() != null) {
						booking.getDependents().addAll(newDependents);
					} else {
						booking.setDependents(newDependents);
					}
					booking.setDependentCount(booking.getDependents().size());

					logger.debug("Added {} dependents to booking.", newDependents.size());
				}

				BigDecimal currentTotal = booking.getTotalAmount();
				if (currentTotal == null) {
					currentTotal = BigDecimal.ZERO;
				}
				BigDecimal newTotal = currentTotal.add(totalAmount);
				booking.setTotalAmount(newTotal);

				logger.debug("Updated total amount: {}", newTotal);

				Booking savedBooking = bookingRepository.save(booking);

				logger.info("Booking saved with ID: {}", savedBooking.getBookingId());

				int seatsToAdd = isNewBooking ? newDependentsCount + 1 : newDependentsCount;
				event.setSeatsBooked(event.getSeatsBooked() + seatsToAdd);
				eventRepository.save(event);

				logger.debug("Updated event seats booked: {}", event.getSeatsBooked());

				return savedBooking;
		

		} catch (Exception e) {
			logger.error("Error saving booking: {}", e.getMessage(), e);
			e.printStackTrace();
			throw new RuntimeException("Error saving booking: " + e.getMessage());
		}
	}

	public List<BookingDto> findAllBookings() {
		logger.info("Fetching all bookings...");
		List<Booking> bookings = bookingRepository.findAll();
		List<BookingDto> bookingDtos = new ArrayList<>();

		for (Booking booking : bookings) {
			BookingDto bookingDto = new BookingDto();
			bookingDto.setBookingId(booking.getBookingId());
			bookingDto.setPaymentVia(booking.getPaymentVia());
			bookingDto.setTotalAmount(booking.getTotalAmount());
			bookingDto.setAmountPaid(booking.getAmountPaid());
			bookingDto.setBookingStatus(booking.getBookingStatus());
			bookingDto.setIsPaid(booking.getIsPaid());
			bookingDto.setBookingDate(booking.getBookingDate());
			bookingDto.setDependentCount(booking.getDependentCount());

			User user = booking.getUserId();
			if (user != null) {
				SignupUserDto userDto = new SignupUserDto();
				userDto.setUserId(user.getUserId());
				userDto.setFullName(user.getFullName());
				userDto.setEmail(user.getEmail());
				userDto.setAge(user.getAge());
				userDto.setPlace(user.getPlace());
				userDto.setPhone(user.getPhone());
				userDto.setGender(user.getGender());
				bookingDto.setUser(userDto);
			}
			Event event = booking.getEventId();
			if (event != null) {
				EventDto eventDto = new EventDto();
				eventDto.setEventId(event.getEventId());
				eventDto.setTitle(event.getTitle());
				eventDto.setDescription(event.getDescription());
				eventDto.setPlace(event.getPlace());
				eventDto.setAdultAmount(event.getAdultAmount());
				eventDto.setChildAmount(event.getChildAmount());
				eventDto.setStartDate(event.getStartDate());
				eventDto.setEndDate(event.getEndDate());
				eventDto.setSeats(event.getSeats());
				eventDto.setSeatsBooked(event.getSeatsBooked());
				eventDto.setStatus(event.getStatus());
				bookingDto.setEvent(eventDto);
			}

			bookingDtos.add(bookingDto);
		}
		logger.info("Found {} bookings.", bookings.size());
		return bookingDtos;
	}

	public Booking updateBooking(BookingDto bookingDto) {
		try {
			logger.info("Updating booking with ID: {}", bookingDto.getBookingId());
			Optional<Booking> optionalBooking = bookingRepository.findById(bookingDto.getBookingId());

			if (optionalBooking.isPresent()) {
				Booking booking = optionalBooking.get();
				booking.setPaymentVia(bookingDto.getPaymentVia());
				booking.setTotalAmount(bookingDto.getTotalAmount());
				booking.setAmountPaid(bookingDto.getAmountPaid());
				booking.setBookingStatus(bookingDto.getBookingStatus());
				booking.setIsPaid(bookingDto.getIsPaid());
				booking.setBookingDate(bookingDto.getBookingDate());
				booking.setDependentCount(bookingDto.getDependentCount());

				User user = userRepository.findById(bookingDto.getUserId())
						.orElseThrow(() -> new RuntimeException("User not found with ID: " + bookingDto.getUserId()));
				logger.info("User not found with ID: {}", bookingDto.getUserId());
				booking.setUserId(user);

				Event event = eventRepository.findById(bookingDto.getEventId().toString())
						.orElseThrow(() -> new RuntimeException("Event not found with ID: " + bookingDto.getEventId()));
				logger.info("Event not found with ID: {}", bookingDto.getEventId());
				booking.setEventId(event);

				return bookingRepository.save(booking);

			} else {
				logger.warn("Booking not found for ID: {}", bookingDto.getBookingId());
				throw new RuntimeException("Booking Not Found");
			}
		} catch (Exception e) {
			logger.error("Error updating booking for ID: {}", bookingDto.getBookingId(), e);
			throw new RuntimeException("Error updating booking: " + e.getMessage());
		}
	}

	public BookingDto getBookingWithDependents(Long bookingId) {
		try {
			logger.info("Fetching booking with dependents for booking ID: {}", bookingId);
			Booking booking = bookingRepository.findById(bookingId)
					.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
			logger.debug("Booking found: {}", booking.getBookingId());

			BookingDto bookingDto = new BookingDto();
			bookingDto.setBookingId(booking.getBookingId());
			bookingDto.setPaymentVia(booking.getPaymentVia());
			bookingDto.setTotalAmount(booking.getTotalAmount());
			bookingDto.setAmountPaid(booking.getAmountPaid());
			bookingDto.setBookingStatus(booking.getBookingStatus());
			bookingDto.setIsPaid(booking.getIsPaid());
			bookingDto.setBookingDate(booking.getBookingDate());
			bookingDto.setDependentCount(booking.getDependentCount());
			bookingDto.setUserId(booking.getUserId().getUserId());
			bookingDto.setEventId(booking.getEventId().getEventId());

			List<DependentDto> dependentDtos = booking.getDependents().stream().map(dependent -> {
				DependentDto dependentDto = new DependentDto();

				dependentDto.setDependentId(dependent.getDependentId());
				dependentDto.setName(dependent.getName());
				dependentDto.setPlace(dependent.getPlace());
				dependentDto.setAge(dependent.getAge());
				dependentDto.setGender(dependent.getGender());
				dependentDto.setRelation(dependent.getRelation());
				dependentDto.setUserId(dependent.getUserId().getUserId());
				dependentDto.setBookingId(dependent.getBookingId().getBookingId());
				return dependentDto;
			}).collect(Collectors.toList());

			Event event = booking.getEventId();
			if (event != null) {
				EventDto eventDto = new EventDto();
				eventDto.setEventId(event.getEventId());
				eventDto.setTitle(event.getTitle());
				eventDto.setDescription(event.getDescription());
				eventDto.setPlace(event.getPlace());
				eventDto.setAdultAmount(event.getAdultAmount());
				eventDto.setChildAmount(event.getChildAmount());
				eventDto.setStartDate(event.getStartDate());
				eventDto.setEndDate(event.getEndDate());
				eventDto.setSeats(event.getSeats());
				eventDto.setSeatsBooked(event.getSeatsBooked());
				eventDto.setStatus(event.getStatus());
				bookingDto.setEvent(eventDto);
			}

			bookingDto.setDependents(dependentDtos);
			logger.info("Booking with dependents fetched successfully for ID: {}", bookingId);
			return bookingDto;

		} catch (Exception e) {
			logger.error("Error fetching booking with dependents for ID: {}", bookingId, e);
			throw new RuntimeException("Error fetching booking: " + e.getMessage());
		}
	}

	public List<BookingDto> findBookingsByEventId(Event eventId) {
		try {
			logger.info("Fetching bookings for event ID: {}", eventId.getEventId());
			List<Booking> bookings = bookingRepository.findByEventId(eventId);
			logger.debug("Found {} bookings for event ID: {}", bookings.size(), eventId.getEventId());

			List<BookingDto> bookingDtos = bookings.stream().map(this::mapToBookingDto).collect(Collectors.toList());
			logger.info("Mapped {} bookings to BookingDtos.", bookingDtos.size());

			return bookingDtos;
		} catch (Exception e) {
			logger.error("Error fetching bookings for event ID: {}", eventId.getEventId(), e);
			throw new RuntimeException("Error fetching bookings: " + e.getMessage());
		}
	}

	public List<BookingDto> findBookingsByUserId(User userId) {
		try {
			logger.info("Fetching bookings for user ID: {}", userId.getUserId());
			List<Booking> bookings = bookingRepository.findByUserId(userId);
			logger.debug("Found {} bookings for user ID: {}", bookings.size(), userId.getUserId());

			List<BookingDto> bookingDtos = bookings.stream().map(this::mapToBookingDto).collect(Collectors.toList());
			logger.info("Mapped {} bookings to BookingDtos.", bookingDtos.size());

			return bookingDtos;
		} catch (Exception e) {
			logger.error("Error fetching bookings for user ID: {}", userId.getUserId(), e);
			throw new RuntimeException("Error fetching bookings: " + e.getMessage());
		}
	}

	public Booking cancelBooking(Long bookingId) {
		try {
			logger.info("Cancelling booking for booking ID: {}", bookingId);
			Booking booking = bookingRepository.findById(bookingId)
					.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

			booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
			logger.info("Booking cancelled successfully for booking ID: {}", bookingId);
			
			BigDecimal amountToSubtract = booking.getEventId().getAdultAmount();			
			booking.setTotalAmount(booking.getTotalAmount().subtract(amountToSubtract));

			return bookingRepository.save(booking);
		} catch (Exception e) {
			logger.error("Error cancelling booking for ID: {}", bookingId, e);
			throw new RuntimeException("Error cancelling booking: " + e.getMessage());
		}
	}

	private BookingDto mapToBookingDto(Booking booking) {

		try {
			logger.debug("Mapping booking ID: {}", booking.getBookingId());

			BookingDto bookingDto = new BookingDto();
			bookingDto.setBookingId(booking.getBookingId());
			bookingDto.setPaymentVia(booking.getPaymentVia());
			bookingDto.setTotalAmount(booking.getTotalAmount());
			bookingDto.setAmountPaid(booking.getAmountPaid());
			bookingDto.setBookingStatus(booking.getBookingStatus());
			bookingDto.setIsPaid(booking.getIsPaid());
			bookingDto.setBookingDate(booking.getBookingDate());
			bookingDto.setDependentCount(booking.getDependentCount());

			User user = booking.getUserId();
			if (user != null) {
				SignupUserDto userDto = new SignupUserDto();
				userDto.setUserId(user.getUserId());
				userDto.setFullName(user.getFullName());
				userDto.setEmail(user.getEmail());
				userDto.setAge(user.getAge());
				userDto.setPlace(user.getPlace());
				userDto.setPhone(user.getPhone());
				userDto.setGender(user.getGender());
				bookingDto.setUser(userDto);
			}

			Event event = booking.getEventId();
			if (event != null) {
				EventDto eventDto = new EventDto();
				eventDto.setEventId(event.getEventId());
				eventDto.setTitle(event.getTitle());
				eventDto.setDescription(event.getDescription());
				eventDto.setPlace(event.getPlace());
				eventDto.setAdultAmount(event.getAdultAmount());
				eventDto.setChildAmount(event.getChildAmount());
				eventDto.setStartDate(event.getStartDate());
				eventDto.setEndDate(event.getEndDate());
				eventDto.setSeats(event.getSeats());
				eventDto.setSeatsBooked(event.getSeatsBooked());
				eventDto.setStatus(event.getStatus());
				bookingDto.setEvent(eventDto);
			}

			List<DependentDto> dependentDtos = booking.getDependents().stream().map(dependent -> {
				DependentDto dependentDto = new DependentDto();
				dependentDto.setDependentId(dependent.getDependentId());
				dependentDto.setName(dependent.getName());
				dependentDto.setPlace(dependent.getPlace());
				dependentDto.setAge(dependent.getAge());
				dependentDto.setGender(dependent.getGender());
				dependentDto.setRelation(dependent.getRelation());
				dependentDto.setUserId(dependent.getUserId().getUserId());
				dependentDto.setBookingId(dependent.getBookingId().getBookingId());
				return dependentDto;
			}).collect(Collectors.toList());
			bookingDto.setDependents(dependentDtos);

			logger.debug("Mapped booking ID: {}", booking.getBookingId());
			return bookingDto;
		} catch (Exception e) {
			logger.error("Error mapping booking ID: {}", booking.getBookingId(), e);
			throw new RuntimeException("Error mapping booking: " + e.getMessage());
		}
	}

	public boolean isUserBookedForEvent(User userId, Event eventId) {
		try {
			return bookingRepository.existsByUserIdAndEventId(userId, eventId);
		} catch (Exception e) {
			throw new RuntimeException("Error checking booking status: " + e.getMessage(), e);
		}
	}

	public byte[] generateBookingsPdf(Long eventId) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			PdfWriter writer = new PdfWriter(outputStream);
			PdfDocument pdfDocument = new PdfDocument(writer);
			Document document = new Document(pdfDocument);

			Event event = eventRepository.findById(eventId.toString())
					.orElseThrow(() -> new RuntimeException("Event not found"));
			List<Booking> bookings = bookingRepository.findByEventId(event);

			document.add(new com.itextpdf.layout.element.Paragraph("SHIEL BIBLE HOME").setBold().setFontSize(16)
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph(" "));
			document.add(new Paragraph(" "));
			document.add(new Paragraph("Bookings List for " + event.getTitle()).setBold().setFontSize(15)
					.setTextAlignment(TextAlignment.CENTER));
			document.add(new Paragraph(" "));

			Table table = new Table(7);
			table.addCell(new Cell().add(new Paragraph(" Name ")).setBold().setTextAlignment(TextAlignment.CENTER));
			table.addCell(new Cell().add(new Paragraph(" Phone ")).setBold().setTextAlignment(TextAlignment.CENTER));
			table.addCell(new Cell().add(new Paragraph(" age ")).setBold().setTextAlignment(TextAlignment.CENTER));
			table.addCell(new Cell().add(new Paragraph(" Place ")).setBold().setTextAlignment(TextAlignment.CENTER));
			table.addCell(new Cell().add(new Paragraph(" Gender ")).setBold().setTextAlignment(TextAlignment.CENTER));
			table.addCell(
					new Cell().add(new Paragraph(" Total Amount ")).setBold().setTextAlignment(TextAlignment.CENTER));
			table.addCell(new Cell().add(new Paragraph(" Status ")).setBold().setTextAlignment(TextAlignment.CENTER));

			for (Booking booking : bookings) {
				table.addCell(" " + booking.getUserId().getFullName() + " ");
				table.addCell(" " + booking.getUserId().getPhone() + " ");
				table.addCell(" " + String.valueOf(booking.getUserId().getAge()) + " ");
				table.addCell(" " + booking.getUserId().getPlace() + " ");
				table.addCell(" " + booking.getUserId().getGender() + " ");
				table.addCell(" " + String.valueOf(booking.getTotalAmount()) + " ");
				table.addCell(" " + booking.getBookingStatus().toString() + " ");

				// Add dependents for this booking
				if (booking.getDependents() != null && !booking.getDependents().isEmpty()) {
					Cell dependentCell = new Cell(1, 7).add(new Paragraph("Family Members"));
					dependentCell.setBold().setTextAlignment(TextAlignment.CENTER);
					table.addCell(dependentCell);

					for (Dependent dependent : booking.getDependents()) {
						String dependentInfo = dependent.getName() + ",   Age: " + dependent.getAge() + ",   Relation: "
								+ dependent.getRelation() + ",   Gender: " + dependent.getGender();
						table.addCell(new Cell(1, 7).add(new Paragraph(dependentInfo)));
					}
				} else {
					table.addCell(new Cell(1, 7).add(new Paragraph("No Family Members")));
				}

				table.addCell(new Cell(1, 7).add(new Paragraph(" ")));
			}

			document.add(table);

			document.close();
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
		}
	}
}
