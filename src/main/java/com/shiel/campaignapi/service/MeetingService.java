package com.shiel.campaignapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.MeetingDto;
import com.shiel.campaignapi.entity.Meeting;
import com.shiel.campaignapi.repository.MeetingRepository;

import jakarta.validation.Valid;

@Service
public class MeetingService {

	private final MeetingRepository meetingRepository;

	public MeetingService(MeetingRepository meetingRepository) {
		this.meetingRepository = meetingRepository;
	}

	public Meeting saveMeeting(MeetingDto meetingDto) {

		Meeting meeting = new Meeting();
		meeting.setTitle(meetingDto.getTitle());
		meeting.setPlace(meetingDto.getPlace());
		meeting.setDescription(meetingDto.getDescription());
		meeting.setDay(meetingDto.getDay());

		return meetingRepository.save(meeting);

	}

	public boolean existsByTitle(String title) {
		return meetingRepository.existsByTitle(title);
	}

	public List<Meeting> findAllMeetings() {
		List<Meeting> meetings = new ArrayList<>();
		meetingRepository.findAll().forEach(meetings::add);
		;
		return meetings;
	}

	public Meeting updateMeeting(MeetingDto meetingDto) {
		try {
			Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingDto.getMeetingId().toString());
			if (optionalMeeting.isPresent()) {
				Meeting meeting = optionalMeeting.get();

				meeting.setTitle(meetingDto.getTitle());
				meeting.setPlace(meetingDto.getPlace());
				meeting.setDescription(meetingDto.getDescription());
				meeting.setDay(meetingDto.getDay());
				return meetingRepository.save(meeting);
			} else {
				throw new RuntimeException("Meeting not found with id " + meetingDto.getMeetingId());
			}

		} catch (Exception e) {
			return null;
		}

	}

	public MeetingDto findMeetingById(@Valid String meetingId) {
		// TODO Auto-generated method stub
		return null;
	}

	public MeetingDto deleteMeetingById(@Valid Long meetingId) {
		Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingId.toString());
		if (optionalMeeting.isPresent()) {
			Meeting meeting = optionalMeeting.get();
			meetingRepository.delete(meeting);

			return mapToMeetingtDto(meeting);
		} else {

			return null;
		}
	}

	private MeetingDto mapToMeetingtDto(Meeting meeting) {
		MeetingDto meetingDto = new MeetingDto();
		meetingDto.setTitle(meeting.getTitle());
		meetingDto.setDescription(meeting.getDescription());
		meetingDto.setPlace(meeting.getPlace());
		meetingDto.setDay(meeting.getDay());
		return meetingDto;

	}

}
