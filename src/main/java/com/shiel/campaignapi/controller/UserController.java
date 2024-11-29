package com.shiel.campaignapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.exception.UserNotFoundException;
import com.shiel.campaignapi.repository.UserRepository;
import com.shiel.campaignapi.service.UserService;

import jakarta.validation.Valid;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
	private final UserService userService;
	@Autowired
	UserRepository userRepository;

	public UserController(UserService userService) {
		this.userService = userService;

	}

	@GetMapping("/all")
	public ResponseEntity<?> allUsers() {
		List<SignupUserDto> users = userService.allUsers();

		return ResponseEntity.ok(users);
	}

	@PostMapping("/update/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable("userId") Integer userId,
			@Valid @RequestBody SignupUserDto userDto) {

		if (userDto == null || userId == null) {
			return ResponseEntity.badRequest().body("Request cannot be empty");
		}

		try {
			int age = userDto.getAge();
			if (age < 15 || age > 100) {
				return ResponseEntity.badRequest().body("Error: Age must be between 15 and 100!");
			}
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().body("Error: Invalid age format!");
		}

		if (!userId.equals(userDto.getUserId())) {
			return ResponseEntity.badRequest().body("Invalid User ID in the request");
		}

		String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		User loggedInUser = userRepository.findByEmail(loggedInUsername)
				.orElseThrow(() -> new RuntimeException("Current user not found with email: " + loggedInUsername));

		if (loggedInUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}

		// Check roles and permissions
		boolean isAdmin = loggedInUser.getRoles().stream().anyMatch(role -> role.getRoleName().equals("ADMIN"));

		if (!isAdmin && !loggedInUser.getUserId().equals(userId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this user");
		}

		try {
			User updatedUser = userService.updateUser(userDto);
			return ResponseEntity.ok(updatedUser);
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	@GetMapping("{userId}")
	public ResponseEntity<?> getUserById(@PathVariable("userId") Integer userId) {
		try {
			SignupUserDto userDto = userService.findUserById(userId);
			return ResponseEntity.ok(userDto);
		} catch (UserNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()); // Correct
		}
	}

}
