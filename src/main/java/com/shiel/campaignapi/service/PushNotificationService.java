package com.shiel.campaignapi.service;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.FirebaseMessaging;

public class PushNotificationService {
	public void sendNotification(String message) {
		try {
			Message notificationMessage = Message.builder()
					.putData("title", "Weekly Meeting Reminder")
					.putData("body", message)
					.setTopic("weekly-meeting").build();

			FirebaseMessaging.getInstance().send(notificationMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
