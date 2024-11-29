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

import com.shiel.campaignapi.dto.MeetingDto;
import com.shiel.campaignapi.entity.Meeting;
import com.shiel.campaignapi.service.MeetingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/meetings")
public class MeetingController {

	@Autowired
	MeetingService meetingService;


	@PostMapping("/add")
	public ResponseEntity<?> addMeeting(@RequestBody MeetingDto meetingDto) {
		if (meetingService.existsByTitle(meetingDto.getTitle())) {
			return ResponseEntity.badRequest().body("Error: Title already Exist");
		}
		Meeting meetingid = meetingService.saveMeeting(meetingDto);
		String message = String.format("Meeting registered successfully!", meetingid);
		return ResponseEntity.ok(message);

	}

	
	@GetMapping("/all")
	public ResponseEntity<?> getAllMeeting() {
		List<Meeting> meetings = meetingService.findAllMeetings();
		if (meetings.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Meetings Found");
		} else {
			return ResponseEntity.ok().body(meetings);
		}
	}


	@PostMapping("/update/{meetingId}")
	public ResponseEntity<?> updateMeeting(@PathVariable("meetingId") Long meetingId,
			@Valid @RequestBody MeetingDto meetingDto) {

		if (meetingId == null || meetingDto == null) {
			return ResponseEntity.badRequest().body("Meeting Id cannot be null");
		}
		if (!meetingId.equals(meetingDto.getMeetingId())) {
			return ResponseEntity.badRequest().body("Invalid Meeting ID in the request");

		}

		Meeting updatedMeeting = meetingService.updateMeeting(meetingDto);
		if (updatedMeeting == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting Not found");
		} else {
			return ResponseEntity.ok().body(updatedMeeting);
		}

	}

	@DeleteMapping("/delete/{meetingId}")
	public ResponseEntity<?> deleteMeetingById(@PathVariable("id") @Valid Long meetingId) {

		if (meetingId == null) {
			return ResponseEntity.badRequest().body("Meeting Id cannot be Null");
		}
		MeetingDto meeting = meetingService.deleteMeetingById(meetingId);
		if (meeting == null) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found with ID: " + meetingId);
		}
		return ResponseEntity.ok().body("Meeting deleted successfully");

	}
	
	@GetMapping("{meetingId}")
	public ResponseEntity<?> getMeetingById(@PathVariable("meetingId") @Valid String meetingId) {
		if (meetingId.isBlank()) {
			return ResponseEntity.badRequest().body("Meeting Id cannot be null");
		}
		MeetingDto meetingDto = meetingService.findMeetingById(meetingId);
		if (meetingDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Meeting found");
		} else {
			return ResponseEntity.ok().body(meetingDto);
		}
	}


}
