package com.shiel.campaignapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.BookingDto;
import com.shiel.campaignapi.dto.DependentDto;
import com.shiel.campaignapi.dto.EventDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Dependent;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.entity.User;
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

	public BookingService(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;

	}

	@Transactional
	public Booking saveBooking(BookingDto bookingDto) {
		try {
			Event event = eventRepository.findById(bookingDto.getEventId().toString())
					.orElseThrow(() -> new RuntimeException("Event not found with ID: " + bookingDto.getEventId()));

			String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			User currentUser = userRepository.findByEmail(currentUserEmail)
					.orElseThrow(() -> new RuntimeException("Current user not found with email: " + currentUserEmail));

			int newDependentsCount = (bookingDto.getDependents() != null) ? bookingDto.getDependents().size() : 0;

			int availableSeats = event.getSeats() - event.getSeatsBooked();
			if (availableSeats <= 0 || newDependentsCount > availableSeats) {
				throw new RuntimeException("Booking is full for this event. No more seats available.");
			}

			Booking booking = bookingRepository.findByUserIdAndEventId(currentUser, event).orElse(null);

			boolean isNewBooking = (booking == null);
			BigDecimal totalAmount = BigDecimal.ZERO;

			if (isNewBooking) {
				totalAmount = totalAmount.add(event.getAdultAmount());
				booking = new Booking(); // Create a new booking for the new user
				booking.setPaymentVia(bookingDto.getPaymentVia());
				booking.setAmountPaid(bookingDto.getAmountPaid());
				booking.setBookingStatus(Booking.BookingStatus.PENDING);
				booking.setIsPaid(bookingDto.getIsPaid());
				booking.setBookingDate(LocalDateTime.now());
				booking.setUserId(currentUser);
				booking.setEventId(event);
			}

			if (bookingDto.getDependents() != null && !bookingDto.getDependents().isEmpty()) {
				for (DependentDto dependentDto : bookingDto.getDependents()) {
					if (dependentDto.getAge() <= 12) {
						totalAmount = totalAmount.add(event.getChildAmount());

					} else {
						totalAmount = totalAmount.add(event.getAdultAmount());

					}
				}

				List<Dependent> newDependents = new ArrayList<>();
				for (DependentDto dependentDto : bookingDto.getDependents()) {
					Dependent dependent = new Dependent();
					dependent.setName(dependentDto.getName());
					dependent.setPlace(dependentDto.getPlace());
					dependent.setGender(dependentDto.getGender());
					dependent.setAge(dependentDto.getAge());
					dependent.setRelation(dependentDto.getRelation());
					dependent.setUserId(currentUser);
					dependent.setBookingId(booking);
					newDependents.add(dependent);
				}
				dependentRepository.saveAll(newDependents);

				if (booking.getDependents() != null) {
					booking.getDependents().addAll(newDependents);
				} else {
					booking.setDependents(newDependents);
				}
				booking.setDependentCount(booking.getDependents().size());
			}

			BigDecimal currentTotal = booking.getTotalAmount();
			if (currentTotal == null) {
				currentTotal = BigDecimal.ZERO;
			}
			BigDecimal newTotal = currentTotal.add(totalAmount);
			booking.setTotalAmount(newTotal);

			Booking savedBooking = bookingRepository.save(booking);

			int seatsToAdd = isNewBooking ? newDependentsCount + 1 : newDependentsCount;
			event.setSeatsBooked(event.getSeatsBooked() + seatsToAdd);
			eventRepository.save(event);

			return savedBooking;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error saving booking: " + e.getMessage());
		}
	}

	public List<BookingDto> findAllBookings() {
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
		return bookingDtos;
	}

	public Booking updateBooking(BookingDto bookingDto) {
		try {
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
				booking.setUserId(user);

				Event event = eventRepository.findById(bookingDto.getEventId().toString())
						.orElseThrow(() -> new RuntimeException("Event not found with ID: " + bookingDto.getEventId()));
				booking.setEventId(event);

				return bookingRepository.save(booking);

			} else {
				throw new RuntimeException("Booking Not Found");
			}
		} catch (Exception e) {
			return null;
		}

	}

	public BookingDto getBookingWithDependents(Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

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

		return bookingDto;
	}

	public List<BookingDto> findBookingsByEventId(Event eventId) {
		List<Booking> bookings = bookingRepository.findByEventId(eventId);
		return bookings.stream().map(this::mapToBookingDto).collect(Collectors.toList());
	}

	public List<BookingDto> findBookingsByUserId(User userId) {
		List<Booking> bookings = bookingRepository.findByUserId(userId);
		return bookings.stream().map(this::mapToBookingDto).collect(Collectors.toList());
	}

	public Booking cancelBooking(Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));

		booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
		return bookingRepository.save(booking);
	}

	private BookingDto mapToBookingDto(Booking booking) {
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

		return bookingDto;
	}
}
