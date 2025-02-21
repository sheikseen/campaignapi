package com.shiel.campaignapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

import com.twilio.rest.api.v2010.account.Message;

@Service
public class SMSService {

	@Value("${twilio.accountSid}")
	private String accountSid;

	@Value("${twilio.authToken}")
	private String authToken;

	@Value("${twilio.phoneNumber}")
	private String twilioPhoneNumber;

	@PostConstruct
	public void init() {
		try {
			System.out.println("Initializing Twilio with Account SID: " + accountSid);
			Twilio.init(accountSid, authToken);
		} catch (Exception e) {
			System.err.println("Error initializing Twilio: " + e.getMessage());
			throw new RuntimeException("Twilio initialization failed", e);
		}
	}

	public void sendOTP(String toPhoneNumber, String otp) {
		try {
			Message.creator(new PhoneNumber(toPhoneNumber),
					new PhoneNumber(twilioPhoneNumber), 
					"Your OTP code is: " + otp 
			).create();
			System.out.println("OTP sent to " + toPhoneNumber);
		} catch (Exception e) {
			System.err.println("Error sending OTP: " + e.getMessage());
		}
	}
}
