package com.shiel.campaignapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.BookingDto;
import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Meeting;
import com.shiel.campaignapi.service.BookingService;

@RestController
@RequestMapping("/booking")
public class BookingController {

	@Autowired
	BookingService bookingService;
	
	@PostMapping("/add")
	public ResponseEntity<?> addBooking(@RequestBody BookingDto bookingDto){
		try {
		Booking  bookingid = bookingService.saveBooking(bookingDto);
		
         return ResponseEntity.ok(bookingid);
		
		  } catch (RuntimeException e) {
	            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	        }
	}
	@GetMapping("/all")
	public ResponseEntity<?> getAllMeeting() {
		List<Booking> bookings = bookingService.findAllBookings();
		if (bookings.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No booking Found");
		} else {
			return ResponseEntity.ok().body(bookings);
		}
	}
}
