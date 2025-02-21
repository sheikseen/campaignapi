package com.shiel.campaignapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.ChangePasswordDto;
import com.shiel.campaignapi.dto.ForgotPasswordDto;
import com.shiel.campaignapi.dto.ResetPasswordDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.PasswordResetToken;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.exception.UserBadRequest;
import com.shiel.campaignapi.exception.UserNotFoundException;
import com.shiel.campaignapi.repository.UserRepository;
import com.shiel.campaignapi.service.UserService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserController {
	private final UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JavaMailSender mailSender;

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

		int age = userDto.getAge();
		if (age < 15 || age > 100) {
			throw new UserBadRequest("Invalid Age", "Age must be between 15 and 100!", 400);
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

//		if (!isAdmin && !loggedInUser.getUserId().equals(userId)) {
//			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this user");
//		}

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

	@DeleteMapping("delete/{userId}")
	public ResponseEntity<Void> softDeleteEntity(@PathVariable Integer userId) {
		Optional<User> user = userRepository.findById(userId);

		if (user.isPresent()) {
			User users = user.get();
			users.setDeletedAt(LocalDateTime.now());
			users.setStatus(User.UserStatus.DEACTIVATED);
			userRepository.save(users);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
		String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		User loggedInUser = userRepository.findByEmail(loggedInUsername)
				.orElseThrow(() -> new RuntimeException("Current user not found with email: " + loggedInUsername));

		if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), loggedInUser.getPassword())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Current password is incorrect");
		}

		loggedInUser.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
		userRepository.save(loggedInUser);
		return ResponseEntity.ok("Password updated successfully");
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {

		PasswordResetToken resetToken = userService.createPasswordResetToken(forgotPasswordDto.getEmail());

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(resetToken.getUser().getEmail());
		message.setSubject("Password Reset Request");
		message.setText("Use this OTP to reset your password:\n\n" + resetToken.getToken());

		mailSender.send(message);

		return ResponseEntity.ok("Password reset email sent");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
		PasswordResetToken token = userService.validatePasswordResetToken(resetPasswordDto.getToken());

		User user = token.getUser();
		user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
		userRepository.save(user);

		userService.invalidatePasswordResetToken(token);
		userService.sendUpdateEmail(user.getEmail(), user.getFullName(), "Password Reset");

		return ResponseEntity.ok("Password reset successfully");
	}
}
