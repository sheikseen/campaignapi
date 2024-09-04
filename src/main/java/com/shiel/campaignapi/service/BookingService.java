package com.shiel.campaignapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.BookingDto;
import com.shiel.campaignapi.dto.EventDto;
import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.entity.Meeting;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.repository.BookingRepository;
import com.shiel.campaignapi.repository.EventRepository;
import com.shiel.campaignapi.repository.UserRepository;

@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	@Autowired
	EventRepository eventRepository;

	@Autowired
	UserRepository userRepository;

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

		return bookingRepository.save(booking);

	}

	public List<Booking> findAllBookings() {
		List<Booking> bookings = new ArrayList<>();
		bookingRepository.findAll().forEach(bookings::add);
		;
		return bookings;
	}

}
