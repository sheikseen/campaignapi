package com.shiel.campaignapi.service;


import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.siel.campaignapi.scheduler.NotificationJob;

import java.time.*;
import java.util.Date;

@Service
public class MeetingSchedulerService {

    @Autowired
    private Scheduler scheduler;

    // Method to calculate the next meeting time
    public ZonedDateTime getNextMeetingTime(String day, LocalTime time, String timeZone) {
      

        // Get the current time in the specified time zone
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // Get the target day of the week
        int currentDayOfWeek = now.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        int targetDayOfWeek = getDayOfWeek(day);

        // Calculate days until the next meeting
        long daysUntilNextMeeting = (targetDayOfWeek - currentDayOfWeek + 7) % 7;
        LocalDate nextMeetingDate = now.toLocalDate().plusDays(daysUntilNextMeeting);

        ZonedDateTime nextMeeting = ZonedDateTime.of(nextMeetingDate, time, zoneId);

        if (now.isAfter(nextMeeting)) {
            nextMeeting = nextMeeting.plusWeeks(1);
        }

        return nextMeeting;
    }

    private int getDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek.toLowerCase()) {
            case "monday":
                return 1;
            case "tuesday":
                return 2;
            case "wednesday":
                return 3;
            case "thursday":
                return 4;
            case "friday":
                return 5;
            case "saturday":
                return 6;
            case "sunday":
                return 7;
            default:
                throw new IllegalArgumentException("Invalid day of the week: " + dayOfWeek);
        }
    }

    // Method to schedule notifications
    public void scheduleMeetingNotification(Integer integer, String place, ZonedDateTime nextMeetingTime) throws SchedulerException {
        ZonedDateTime utcTime = nextMeetingTime.withZoneSameInstant(ZoneId.of("UTC"));

        // Create a Quartz Job for the notification
        JobDetail jobDetail = JobBuilder.newJob(NotificationJob.class)
                .withIdentity("meeting_" + integer)
                .usingJobData("place", place)
                .build();

        // Create a trigger to fire at the next meeting time
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger_" + integer)
                .startAt(Date.from(utcTime.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}


