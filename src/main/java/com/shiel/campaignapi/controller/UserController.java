package com.shiel.campaignapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.EventDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<User> authenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		User currentUser = (User) authentication.getPrincipal();

		return ResponseEntity.ok(currentUser);
	}

	@GetMapping
	public ResponseEntity<List<User>> allUsers() {
		List<User> users = userService.allUsers();

		return ResponseEntity.ok(users);
	}

	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateUser(@PathVariable("id") Integer userId, @Valid @RequestBody SignupUserDto userDto) {

		
		if (userDto == null || userId == null) {
			return ResponseEntity.badRequest().body("Request cannot be empty ");
		}
		if (!userId.equals(userDto.getUserId())) {
			return ResponseEntity.badRequest().body("Invalid User ID in the request");
		}

		User updatedUser = userService.updateUser(userDto);
		if (updatedUser == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No User found");
		} else {
			return ResponseEntity.ok().body(updatedUser);
		}

	}
}
