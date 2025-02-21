package com.siel.campaignapi.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shiel.campaignapi.service.PushNotificationService;

@Component
public class NotificationJob implements Job {

    @Autowired
    private PushNotificationService pushNotificationService;

    @Override
    public void execute(JobExecutionContext context) {
        String meetingTitle = context
        		.getJobDetail()
        		.getJobDataMap()
        		.getString("place");
        pushNotificationService.sendNotification("Meeting started: " + meetingTitle);
    }
}
