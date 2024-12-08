/*package com.shiel.campaignapi.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            String order = paymentService.createOrder(
                (Integer) data.get("amount"), 
                (String) data.get("currency")
            );
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

*
*
	public Booking saveBooking(BookingDto bookingDto) {
		try {

			Event event = eventRepository.findById(bookingDto.getEventId().toString())
					.orElseThrow(() -> new RuntimeException("Event not found with ID: " + bookingDto.getEventId()));

			String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			User currentUser = userRepository.findByEmail(currentUserEmail)
					.orElseThrow(() -> new RuntimeException("Current user not found with email: " + currentUserEmail));
			
			if (event.getSeats() <= event.getSeatsBooked()) {
				throw new RuntimeException("Booking is full for this event. No more seats available.");
			}
			Booking booking = new Booking();

			booking.setPaymentVia(bookingDto.getPaymentVia());
			booking.setTotalAmount(bookingDto.getTotalAmount());
			booking.setAmountPaid(bookingDto.getAmountPaid());
			booking.setBookingStatus(Booking.BookingStatus.PENDING);
			booking.setIsPaid(bookingDto.getIsPaid());
			booking.setBookingDate(LocalDateTime.now());
			booking.setUserId(currentUser);
			booking.setEventId(event);

			Booking savedBooking = bookingRepository.save(booking);

			if (bookingDto.getDependents() != null && !bookingDto.getDependents().isEmpty()) {
				List<Dependent> dependents = new ArrayList<>();
				for (DependentDto dependentDto : bookingDto.getDependents()) {

					Dependent dependent = new Dependent();
					dependent.setName(dependentDto.getName());
					dependent.setPlace(dependentDto.getPlace());
					dependent.setGender(dependentDto.getGender());
					dependent.setAge(dependentDto.getAge());
					dependent.setRelation(dependentDto.getRelation());
					dependent.setUserId(currentUser);
					dependent.setBookingId(savedBooking);

					dependents.add(dependent);
				}

				dependentRepository.saveAll(dependents);
				savedBooking.setDependents(dependents);
				savedBooking.setDependentCount(dependents.size());
				savedBooking = bookingRepository.save(savedBooking);
			}

			int totalSeatsBooked = calculateSeatsBooked(event);
			event.setSeatsBooked(totalSeatsBooked);
			eventRepository.save(event);
			return savedBooking;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error saving booking: " + e.getMessage());
		}
	}
*
*
*/


//import java.util.List;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import com.shiel.campaignapi.dto.BookingDto;
//import com.shiel.campaignapi.dto.DependentDto;
//import com.shiel.campaignapi.entity.Booking;
//import com.shiel.campaignapi.entity.Dependent;
//import com.shiel.campaignapi.entity.Event;
//import com.shiel.campaignapi.entity.User;
