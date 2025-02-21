package com.shiel.campaignapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.BookingDto;
import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/booking")
public class BookingController {

	@Autowired
	BookingService bookingService;

	@PostMapping("/add")
	public ResponseEntity<?> addBooking(@RequestBody BookingDto bookingDto) {
		Booking booking = bookingService.saveBooking(bookingDto);
		return ResponseEntity.ok(booking);
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllBooking() {
		List<BookingDto> bookings = bookingService.findAllBookings();
		if (bookings.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No bookings found");
		}
		return ResponseEntity.ok(bookings);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<BookingDto> getBookingWithDependents(@PathVariable Long bookingId) {
		BookingDto bookingDto = bookingService.getBookingWithDependents(bookingId);
		return ResponseEntity.ok(bookingDto);
	}

	@PostMapping("/update/{id}")
	public ResponseEntity<Booking> updateBooking(@PathVariable("id") Long bookingId,
			@Valid @RequestBody BookingDto bookingDto) {
		if (!bookingId.equals(bookingDto.getBookingId())) {
			return ResponseEntity.badRequest().body(null);
		}

		Booking updatedBooking = bookingService.updateBooking(bookingDto);
		return ResponseEntity.ok(updatedBooking);
	}

	@GetMapping("/event/{eventId}")
	public ResponseEntity<?> getBookingsByEventId(@PathVariable Event eventId) {
		List<BookingDto> bookings = bookingService.findBookingsByEventId(eventId);
		if (bookings.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No bookings found for the specified event");
		} else {
			return ResponseEntity.ok(bookings);
		}
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getBookingsByUserId(@PathVariable User userId) {
		List<BookingDto> bookings = bookingService.findBookingsByUserId(userId);
		if (bookings.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No bookings found for the specified User");
		} else {
			return ResponseEntity.ok(bookings);
		}
	}

	@DeleteMapping("/delete/{bookingId}")
	public ResponseEntity<Booking> cancelBooking(@PathVariable Long bookingId) {
		Booking updatedBooking = bookingService.cancelBooking(bookingId);
		return ResponseEntity.ok(updatedBooking);
	}

	@GetMapping("/isUserBooked")
	public ResponseEntity<Boolean> isUserBookedForEvent(@RequestParam User userId, @RequestParam Event eventId) {
		boolean isBooked = bookingService.isUserBookedForEvent(userId, eventId);
		return ResponseEntity.ok(isBooked);
	}

	@GetMapping("/download/{eventId}")
	public ResponseEntity<byte[]> downloadAllBookings(@PathVariable Long eventId) {
		byte[] pdfData = bookingService.generateBookingsPdf(eventId);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bookings.pdf");
		headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

		return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
	}
}
