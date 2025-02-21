package com.shiel.campaignapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.service.SMSService;

@RestController
@RequestMapping("/otp")
public class OTPController {

	@Autowired
	private SMSService smsService;
	private final Map<String, String> otpCache = new HashMap<>();

	@PostMapping("/generate")
	public String generateAndSendOTP(@RequestParam String phoneNumber) {
		String otp = generateOTP();

		smsService.sendOTP(phoneNumber, otp);
		otpCache.put(phoneNumber, otp);

		return "OTP sent to " + phoneNumber;
	}

	@PostMapping("/validate")
	public String validateOTP(@RequestParam String phoneNumber, @RequestParam String otp) {
		 String storedOtp = otpCache.get(phoneNumber);


		 if (storedOtp != null && storedOtp.equals(otp)) {
	            otpCache.remove(phoneNumber); 
	            return "OTP validated successfully!";
	        } else {
	            return "Invalid OTP!";
	        }
	}

	private String generateOTP() {
		return String.format("%06d", (int) (Math.random() * 1000000));
	}

}
