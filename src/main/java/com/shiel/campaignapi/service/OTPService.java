package com.shiel.campaignapi.service;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class OTPService {
	 private static final int OTP_LENGTH = 6;
	 public String generateOTP() {
	        Random rand = new Random();
	        StringBuilder otp = new StringBuilder();
	        for (int i = 0; i < OTP_LENGTH; i++) {
	            otp.append(rand.nextInt(10));
	        }
	        return otp.toString();
	    }
}
