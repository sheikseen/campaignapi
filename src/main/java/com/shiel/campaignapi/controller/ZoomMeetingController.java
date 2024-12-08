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

import com.shiel.campaignapi.dto.ZoomMeetingDto;
import com.shiel.campaignapi.entity.ZoomMeetings;
import com.shiel.campaignapi.service.ZoomMeetingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/zoom-meetings")
public class ZoomMeetingController {

	@Autowired
	private ZoomMeetingService zoomMeetingService;

	@PostMapping("/add")
	public ResponseEntity<?> addMeeting(@RequestBody ZoomMeetingDto zoomDto) {
		if (zoomMeetingService.existsByPlace(zoomDto.getPlace())) {
			return ResponseEntity.badRequest().body("Error: Title already exists");
		}
		ZoomMeetings meeting = zoomMeetingService.saveMeeting(zoomDto);
		return ResponseEntity.ok("Zoom meeting registered successfully with ID: " + meeting.getMeetingId());
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllMeetings() {
		List<ZoomMeetings> meetings = zoomMeetingService.findAllMeetings();
		if (meetings.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Zoom meetings found");
		}
		return ResponseEntity.ok(meetings);
	}

	@PostMapping("/update/{meetingId}")
	public ResponseEntity<?> updateMeeting(@PathVariable("meetingId") Integer meetingId,
			@Valid @RequestBody ZoomMeetingDto zoomDto) {
		if (meetingId == null || zoomDto == null) {
			return ResponseEntity.badRequest().body("Meeting ID or meeting details cannot be null");
		}
		if (!meetingId.equals(zoomDto.getMeetingId())) {
			return ResponseEntity.badRequest().body("Invalid Meeting ID in the request");
		}
		ZoomMeetings updatedMeeting = zoomMeetingService.updateMeeting(zoomDto);
		if (updatedMeeting == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Zoom meeting not found");
		}
		return ResponseEntity.ok(updatedMeeting);
	}

	@DeleteMapping("/delete/{meetingId}")
	public ResponseEntity<?> deleteMeetingById(@PathVariable("meetingId") Integer meetingId) {
		if (meetingId == null) {
			return ResponseEntity.badRequest().body("Meeting ID cannot be null");
		}
		ZoomMeetingDto zoomMeetingDto = zoomMeetingService.deleteMeetingById(meetingId);
		if (zoomMeetingDto == null) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meeting not found with ID: " + meetingId);
		}
		return ResponseEntity.ok("Zoom meeting deleted successfully");
	}

	@GetMapping("/{meetingId}")
	public ResponseEntity<?> getMeetingById(@PathVariable("meetingId") Integer meetingId) {
		if (meetingId == null) {
			return ResponseEntity.badRequest().body("Meeting ID cannot be null");
		}
		ZoomMeetingDto zoomDto = zoomMeetingService.findZoomMeetingById(meetingId);
		if (zoomDto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Zoom meeting not found");
		}
		return ResponseEntity.ok(zoomDto);
	}
	
	@GetMapping("/place/{place}")
    public ResponseEntity<?> getMeetingsByPlace(@PathVariable("place") String place) {
        List<ZoomMeetingDto> meetings = zoomMeetingService.findMeetingsByPlace(place);
        if (meetings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No meetings found for the specified place");
        }
        return ResponseEntity.ok(meetings);
    }
}
