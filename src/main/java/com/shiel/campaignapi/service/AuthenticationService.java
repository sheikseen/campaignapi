package com.shiel.campaignapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.SigninUserDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.Role;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.repository.RoleRepository;
import com.shiel.campaignapi.repository.UserRepository;

@Service
public class AuthenticationService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;

	}

	public User signup(SignupUserDto signupUserDto) {
		var user = new User().setFullName(signupUserDto.getFullName()).setEmail(signupUserDto.getEmail())
				.setPassword(passwordEncoder.encode(signupUserDto.getPassword())).setAge(signupUserDto.getAge())
				.setGender(signupUserDto.getGender()).setPhone(signupUserDto.getPhone())
				.setPlace(signupUserDto.getPlace());

		List<Role> roles = new ArrayList<>();

		if (signupUserDto.getRoleId() != null) {

			Role assignedRole = roleRepository.findById(signupUserDto.getRoleId())
					.orElseThrow(() -> new RuntimeException("Role not found with ID: " + signupUserDto.getRoleId()));
			roles.add(assignedRole);
		} else {

			Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
					.orElseThrow(() -> new RuntimeException("Default role 'USER' not found"));
			roles.add(defaultRole);
		}

		user.setRoles(roles);
		user = userRepository.save(user);

		return user;
	}

	public User authenticate(SigninUserDto signinUserDto) {
		// Determine if the input is an email or phone number
		String identifier = signinUserDto.getIdentifier();

		if (identifier == null || signinUserDto.getPassword() == null) {
			throw new IllegalArgumentException("Email/Phone number or password cannot be null");
		}

		Optional<User> optionalUser;

		if (isPhone(identifier)) {
			optionalUser = userRepository.findByPhone(identifier);
		} else if (isEmail(identifier)) {
			optionalUser = userRepository.findByEmail(identifier);
		} else {
			throw new IllegalArgumentException("Invalid identifier format. Must be a valid email or phone number.");
		}
		User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), signinUserDto.getPassword()));

		return user;
	}

	public List<User> allUsers() {
		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);
		return users;
	}

	public boolean existsByPhone(String phone) {
		return userRepository.existsByPhone(phone);
	}

	private boolean isEmail(String input) {
		return input.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
	}

	// Helper method to validate phone number (basic example)
	private boolean isPhone(String input) {
		return input.matches("^\\+?[1-9]\\d{1,14}$"); // E.164 format
	}

}
