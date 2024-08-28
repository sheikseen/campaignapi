package com.shiel.campaignapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.SigninUserDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.response.SigninResponse;
import com.shiel.campaignapi.service.AuthenticationService;
import com.shiel.campaignapi.service.JwtService;


@RequestMapping("/auth")
@RestController
public class AuthenticationController {
	private final JwtService jwtService;
	private final AuthenticationService authenticationService;

	public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
	}

	@PostMapping("/signup")
	public ResponseEntity<User> register(@RequestBody SignupUserDto signupUserDto) {
		User registeredUser = authenticationService.signup(signupUserDto);

		return ResponseEntity.ok(registeredUser);
	}

	@PostMapping("/signin")
	public ResponseEntity<SigninResponse> authenticate(@RequestBody SigninUserDto signUserDto) {
		User authenticatedUser = authenticationService.authenticate(signUserDto);

		String jwtToken = jwtService.generateToken(authenticatedUser);

		SigninResponse loginResponse = new SigninResponse().setToken(jwtToken)
				.setExpiresIn(jwtService.getExpirationTime());

		return ResponseEntity.ok(loginResponse);
	}
}