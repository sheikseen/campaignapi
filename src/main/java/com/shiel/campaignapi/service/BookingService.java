package com.shiel.campaignapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.BookingDto;
import com.shiel.campaignapi.dto.DependentDto;
import com.shiel.campaignapi.dto.EventDto;
import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Dependent;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.entity.Meeting;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.repository.BookingRepository;
import com.shiel.campaignapi.repository.DependentRepository;
import com.shiel.campaignapi.repository.EventRepository;
import com.shiel.campaignapi.repository.UserRepository;

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

	public Booking saveBooking(BookingDto bookingDto) {

		Booking booking = new Booking();

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

		Booking savedBooking = bookingRepository.save(booking);

		// Save dependents if provided

		if (bookingDto.getDependents() != null && !bookingDto.getDependents().isEmpty()) {
			List<Dependent> dependents = new ArrayList<>();
			for (DependentDto dependentDto : bookingDto.getDependents()) {

				Dependent dependent = new Dependent();
				dependent.setName(dependentDto.getName());
				dependent.setPlace(dependentDto.getPlace());
				dependent.setGender(dependentDto.getGender());
				dependent.setAge(dependentDto.getAge());
				dependent.setRelation(dependentDto.getRelation());
				
				User dependentUser = userRepository.findById(dependentDto.getUserId()).orElseThrow(
						() -> new RuntimeException("Dependent user not found with ID: " + dependentDto.getUserId()));
				dependent.setUserId(dependentUser);
			

				dependent.setBookingId(savedBooking);
				dependents.add(dependent);
			}
			dependentRepository.saveAll(dependents);
		}
		return savedBooking;
	}

	public List<Booking> findAllBookings() {
		List<Booking> bookings = new ArrayList<>();
		bookingRepository.findAll().forEach(bookings::add);
		;
		return bookings;
	}

	public Booking updateBooking(BookingDto bookingDto) {
		try {
			Optional<Booking> optionalBooking = bookingRepository.findById(bookingDto.getBookingId().toString());

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
		  Booking booking = bookingRepository.findById(bookingId.toString())
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
	        dependentDto.setRelation(dependent.getRelation());
	        dependentDto.setUserId(dependent.getUserId().getUserId()); 
	        dependentDto.setBookingId(dependent.getBookingId().getBookingId()); 
	        return dependentDto;
	    }).collect(Collectors.toList());

	    bookingDto.setDependents(dependentDtos);

	    return bookingDto;
	}

}
