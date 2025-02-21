package com.siel.campaignapi.scheduler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shiel.campaignapi.entity.ZoomMeetings;
import com.shiel.campaignapi.repository.ZoomMeetingRepository;
import com.shiel.campaignapi.service.MeetingSchedulerService;

import java.time.ZonedDateTime;

import java.util.List;

@Component
public class MeetingSchedulerInitializer implements CommandLineRunner {

	private final ZoomMeetingRepository zoomMeetingRepository;
	private final MeetingSchedulerService meetingSchedulerService;

	public MeetingSchedulerInitializer(ZoomMeetingRepository zoomMeetingRepository,
			MeetingSchedulerService meetingSchedulerService) {
		this.zoomMeetingRepository = zoomMeetingRepository;
		this.meetingSchedulerService = meetingSchedulerService;
	}

	@Override
	public void run(String... args) throws Exception {
		List<ZoomMeetings> zoomMeetings = zoomMeetingRepository.findAll();
		for (ZoomMeetings zoomMeeting: zoomMeetings) {
			ZonedDateTime nextMeetingTime = meetingSchedulerService.getNextMeetingTime(zoomMeeting.getDay(),
					zoomMeeting.getTime(), zoomMeeting.getTimeZone());

			meetingSchedulerService.scheduleMeetingNotification(zoomMeeting.getMeetingId(), zoomMeeting.getPlace(), nextMeetingTime);
		}

	}
}
