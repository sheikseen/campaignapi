package com.shiel.campaignapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.shiel.campaignapi.dto.ZoomMeetingDto;
import com.shiel.campaignapi.entity.ZoomMeetings;
import com.shiel.campaignapi.repository.ZoomMeetingRepository;

import jakarta.validation.Valid;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ZoomMeetingService {

	@Autowired
	private final ZoomMeetingRepository zoomMeetingRepository;
	private static final Logger logger = LoggerFactory.getLogger(ZoomMeetingService.class);
	private final MeetingSchedulerService meetingSchedulerService;

	public ZoomMeetingService(ZoomMeetingRepository zoomMeetingRepository,
			MeetingSchedulerService meetingSchedulerService) {
		this.zoomMeetingRepository = zoomMeetingRepository;
		this.meetingSchedulerService = meetingSchedulerService;
	}

	public boolean existsByPlace(String place) {
		return zoomMeetingRepository.existsByPlace(place);
	}

	public ZoomMeetings saveMeeting(ZoomMeetingDto zoomDto) {
		logger.info("Saving Zoom Meetings");
		try {
			String timeZone = zoomDto.getTimeZone();
			ZoneId zoneId;

			try {
				zoneId = ZoneId.of(timeZone);
			} catch (DateTimeException e) {
				logger.error("Invalid time zone: {}", timeZone, e);
				throw new IllegalArgumentException("Invalid time zone: " + timeZone, e);
			}

			// ZonedDateTime now = ZonedDateTime.now(zoneId);
			// Parse the meeting time from the DTO

			LocalTime meetingTime = zoomDto.getTime();
			if (meetingTime == null) {
				logger.error("Meeting time is required");
				throw new IllegalArgumentException("Meeting time cannot be null");
			}

			// Delegate to MeetingSchedulerService to calculate next meeting time
			ZonedDateTime nextMeeting = meetingSchedulerService.getNextMeetingTime(zoomDto.getDay(), meetingTime,
					timeZone);

			if (nextMeeting == null) {
				logger.error("Failed to determine the next meeting time");
				throw new RuntimeException("Could not schedule the next meeting");
			}

			logger.info("Next meeting scheduled for: {}", nextMeeting);

			// Create and save ZoomMeeting entity without storing next meeting time
			ZoomMeetings zoomMeeting = new ZoomMeetings();
			zoomMeeting.setPlace(zoomDto.getPlace());
			zoomMeeting.setDescription(zoomDto.getDescription());
			zoomMeeting.setDay(zoomDto.getDay());
			zoomMeeting.setZoomId(zoomDto.getZoomId());
			zoomMeeting.setZoomLink(zoomDto.getZoomLink());
			zoomMeeting.setTime(meetingTime);
			zoomMeeting.setTimeZone(timeZone);
			zoomMeeting.setDistrict(zoomDto.getDistrict());

			return zoomMeetingRepository.save(zoomMeeting);

		} catch (IllegalArgumentException e) {
			logger.error("Validation error while saving Zoom meeting", e);
			throw e;
		} catch (Exception e) {
			logger.error("Error occurred while saving Zoom meeting", e);
			throw new RuntimeException("Failed to save Zoom meeting", e);
		}
	}

	public List<ZoomMeetings> findAllMeetings() {
		return zoomMeetingRepository.findAll();
	}

	public ZoomMeetings updateMeeting(ZoomMeetingDto zoomMeetingDto) {
		logger.info("update Zoom Meetings");
		try {
			Optional<ZoomMeetings> existingMeeting = zoomMeetingRepository
					.findById(zoomMeetingDto.getMeetingId().toString());
			if (existingMeeting.isPresent()) {
				ZoomMeetings zoomMeeting = existingMeeting.get();
				zoomMeeting.setPlace(zoomMeetingDto.getPlace());
				zoomMeeting.setDescription(zoomMeetingDto.getDescription());
				zoomMeeting.setDay(zoomMeetingDto.getDay());
				zoomMeeting.setZoomId(zoomMeetingDto.getZoomId());
				zoomMeeting.setZoomLink(zoomMeetingDto.getZoomLink());
				zoomMeeting.setTime(zoomMeetingDto.getTime());
				zoomMeeting.setTimeZone(zoomMeetingDto.getTimeZone());
				zoomMeeting.setDistrict(zoomMeetingDto.getDistrict());
				return zoomMeetingRepository.save(zoomMeeting);
			} else {
				throw new RuntimeException("Meeting not found with id " + zoomMeetingDto.getMeetingId());
			}

		} catch (Exception e) {
			logger.error("Error occurred while update zoom meeting", e);
			throw new RuntimeException("Failed to update zoom meeting", e);
		}
	}

	public ZoomMeetingDto findZoomMeetingById(@Valid Integer meetingId) {
		return zoomMeetingRepository.findById(meetingId.toString()).map(this::mapToZoomMeetingtDto).orElse(null);

	}

	public ZoomMeetingDto deleteMeetingById(@Valid Integer meetingId) {
		Optional<ZoomMeetings> optionalMeeting = zoomMeetingRepository.findById(meetingId.toString());
		if (optionalMeeting.isPresent()) {
			ZoomMeetings zoomMeeting = optionalMeeting.get();
			zoomMeetingRepository.delete(zoomMeeting);

			return mapToZoomMeetingtDto(zoomMeeting);
		} else {
			return null;
		}
	}

	public List<ZoomMeetingDto> findMeetingsByPlace(String place) {
		List<ZoomMeetings> zoomMeetings = zoomMeetingRepository.findByPlace(place);
		return zoomMeetings.stream().map(this::mapToZoomMeetingtDto).collect(Collectors.toList());
	}

	private ZoomMeetingDto mapToZoomMeetingtDto(ZoomMeetings zoomMeeting) {
		ZoomMeetingDto zoomMeetingDto = new ZoomMeetingDto();
		zoomMeetingDto.setPlace(zoomMeeting.getPlace());
		zoomMeetingDto.setDescription(zoomMeeting.getDescription());
		zoomMeetingDto.setDay(zoomMeeting.getDay());
		zoomMeetingDto.setZoomId(zoomMeeting.getZoomId());
		zoomMeetingDto.setZoomLink(zoomMeeting.getZoomLink());
		zoomMeetingDto.setTime(zoomMeeting.getTime());
		zoomMeetingDto.setTimeZone(zoomMeeting.getTimeZone());
		zoomMeetingDto.setDistrict(zoomMeeting.getDistrict());
		return zoomMeetingDto;

	}

	public String getLocalMeetingTime(String utcDateTime, String userTimeZone) {
		ZonedDateTime zonedUtc = ZonedDateTime.parse(utcDateTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
		ZonedDateTime localDateTime = zonedUtc.withZoneSameInstant(ZoneId.of(userTimeZone));
		return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
	}

}
