package com.shiel.campaignapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.MeetingDto;
import com.shiel.campaignapi.entity.Meeting;
import com.shiel.campaignapi.repository.MeetingRepository;

import jakarta.validation.Valid;

@Service
public class MeetingService {

	private final MeetingRepository meetingRepository;
	private static final Logger logger = LoggerFactory.getLogger(MeetingService.class);

	public MeetingService(MeetingRepository meetingRepository) {
		this.meetingRepository = meetingRepository;
	}

	public Meeting saveMeeting(MeetingDto meetingDto) {
		logger.info("Save meeting: {}", meetingDto);
		try {
			Meeting meeting = new Meeting();
			meeting.setTitle(meetingDto.getTitle());
			meeting.setPlace(meetingDto.getPlace());
			meeting.setDescription(meetingDto.getDescription());
			meeting.setDay(meetingDto.getDay());
			meeting.setMap(meetingDto.getMap());
			meeting.setImage(meetingDto.getImage());
			return meetingRepository.save(meeting);
		} catch (Exception e) {
			logger.error("Error saving meeting: {}", meetingDto, e);
			throw new RuntimeException("Error saving meeting", e);
		}
	}

	public boolean existsByTitle(String title) {
		return meetingRepository.existsByTitle(title);
	}

	public List<Meeting> findAllMeetings() {
		List<Meeting> meetings = new ArrayList<>();
		meetingRepository.findAll().forEach(meetings::add);
		return meetings;
	}

	public Meeting updateMeeting(MeetingDto meetingDto) {
		logger.info("Updating meeting: {}", meetingDto);
		try {
			Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingDto.getMeetingId().toString());
			if (optionalMeeting.isPresent()) {
				Meeting meeting = optionalMeeting.get();

				meeting.setTitle(meetingDto.getTitle());
				meeting.setPlace(meetingDto.getPlace());
				meeting.setDescription(meetingDto.getDescription());
				meeting.setDay(meetingDto.getDay());
				meeting.setMap(meetingDto.getMap());
				meeting.setImage(meetingDto.getImage());
				return meetingRepository.save(meeting);
			} else {
				logger.warn("Meeting not found with ID: {}", meetingDto.getMeetingId());
				throw new RuntimeException("Meeting not found with id " + meetingDto.getMeetingId());
			}

		} catch (Exception e) {
			logger.error("Error updating meeting: {}", meetingDto, e);
			throw new RuntimeException("Error updating meeting", e);
		}

	}

	public MeetingDto findMeetingById(@Valid String meetingId) {
		return meetingRepository.findById(meetingId).map(this::mapToMeetingtDto).orElse(null);

	}

	public MeetingDto deleteMeetingById(@Valid Long meetingId) {
		logger.info("Deleting meeting by ID: {}", meetingId);
		try {
			Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingId.toString());
			if (optionalMeeting.isPresent()) {
				Meeting meeting = optionalMeeting.get();
				meetingRepository.delete(meeting);

				return mapToMeetingtDto(meeting);
			} else {

				return null;
			}
		} catch (Exception e) {
			logger.error("Error deleting meeting by ID: {}", meetingId, e);
			throw new RuntimeException("Error deleting meeting", e);
		}
	}

	private MeetingDto mapToMeetingtDto(Meeting meeting) {
		MeetingDto meetingDto = new MeetingDto();
		meetingDto.setTitle(meeting.getTitle());
		meetingDto.setDescription(meeting.getDescription());
		meetingDto.setPlace(meeting.getPlace());
		meetingDto.setDay(meeting.getDay());
		meetingDto.setMap(meeting.getMap());
		meetingDto.setImage(meeting.getImage());
		return meetingDto;

	}

}
