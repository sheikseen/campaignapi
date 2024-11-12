package com.shiel.campaignapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.BookingDto;
import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Meeting;
import com.shiel.campaignapi.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/booking")
public class BookingController {

	@Autowired
	BookingService bookingService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/add")
	public ResponseEntity<?> addBooking(@RequestBody BookingDto bookingDto) {
		try {
			Booking bookingid = bookingService.saveBooking(bookingDto);

			return ResponseEntity.ok(bookingid);

		} catch (RuntimeException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllBooking() {
		List<BookingDto> bookings = bookingService.findAllBookings();
		if (bookings.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No booking Found");
		} else {
			return ResponseEntity.ok().body(bookings);
		}
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<BookingDto> getBookingWithDependents(@PathVariable Long bookingId) {
		BookingDto bookingDto = bookingService.getBookingWithDependents(bookingId);
		return ResponseEntity.ok(bookingDto);
	}

	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateBooking(@PathVariable("id") Long bookingId,
			@Valid @RequestBody BookingDto bookingDto) {
		if (bookingId == null || bookingDto == null) {
			return ResponseEntity.badRequest().body("Booking Id cannot be Null");
		}
		if (!bookingId.equals(bookingDto.getBookingId())) {
			return ResponseEntity.badRequest().body("Invalid Booking ID in the request");

		}

		Booking updateBooking = bookingService.updateBooking(bookingDto);
		if (updateBooking == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking Not found");
		} else {
			return ResponseEntity.ok().body(updateBooking);
		}

	}
}
