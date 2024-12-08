package com.shiel.campaignapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.EventDto;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.service.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class EventController {

	@Autowired
	EventService eventService;

	@PostMapping("/add")
	public ResponseEntity<?> addEvent(@RequestBody EventDto eventDto) {
		if (eventService.existsByTitle(eventDto.getTitle())) {
			return ResponseEntity.badRequest().body("Error: event title is already taken!");
		}
		if (!eventService.isValidDate(eventDto.getStartDate(), eventDto.getEndDate())) {
			return ResponseEntity.badRequest()
					.body("Error: start date must be before end date and both dates must be in the future");
		}
		if (eventDto.getChildAmount().compareTo(eventDto.getAdultAmount()) > 0) {
			return ResponseEntity.badRequest()
					.body("Child amount cannot be more than adult amount.");
		}
		String eventid = eventService.saveEvent(eventDto);
		String message = String.format("Event %s registered successfully!", eventid);
		return ResponseEntity.ok(message);
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllEvent() {
		List<EventDto> events = eventService.findAllEvents();
		if (events.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Events found");
		} else {
			return ResponseEntity.ok().body(events);
		}

	}

	@GetMapping("{eventId}")
	public ResponseEntity<?> getEventById(@PathVariable("eventId") @Valid String eventId) {
		if (eventId.isBlank()) {
			return ResponseEntity.badRequest().body("Event Id cannot be null");
		}
		EventDto eventDto = eventService.findEventById(eventId);
		if (eventDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Events found");
		} else {
			return ResponseEntity.ok().body(eventDto);
		}
	}

	@PostMapping("/update/{eventId}")
	public ResponseEntity<?> updateEvent(@PathVariable("eventId") @Valid Long eventId,
			@Valid @RequestBody EventDto eventDto) {

		if (eventDto == null || eventId == null) {
			return ResponseEntity.badRequest().body("Request cannot be empty ");
		}

		if (!eventId.equals(eventDto.getEventId())) {
			return ResponseEntity.badRequest().body("Invalid Meeting ID in the request");

		}

		if (!eventService.isValidDate(eventDto.getStartDate(), eventDto.getEndDate())) {
			return ResponseEntity.badRequest()
					.body("Error: start date must be before end date and both dates must be in the future");
		}

		Event event = eventService.updateEvent(eventDto);
		if (event == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Event found");
		} else {
			return ResponseEntity.ok().body(event);
		}

	}

	@DeleteMapping("/delete/{eventId}")
	public ResponseEntity<?> deleteUserById(@PathVariable("eventId") @Valid String eventId) {
		if (eventId.isBlank()) {
			return ResponseEntity.badRequest().body("Event ID cannot be empty");
		}
		EventDto event = eventService.deleteEventById(eventId);
		if (event == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found with ID: " + eventId);

		}
		return ResponseEntity.ok().body("Event deleted successfully");
	}

}
