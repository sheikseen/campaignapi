package com.shiel.campaignapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.SigninUserDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.Country;
import com.shiel.campaignapi.entity.Role;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.exception.UserBadCredential;
import com.shiel.campaignapi.exception.UserIllegalArgumentException;
import com.shiel.campaignapi.exception.UserNotFoundException;
import com.shiel.campaignapi.repository.CountryRepository;
import com.shiel.campaignapi.repository.RoleRepository;
import com.shiel.campaignapi.repository.UserRepository;

@Service
public class AuthenticationService {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	private final UserRepository userRepository;
	private final CountryRepository countryRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder, RoleRepository roleRepository, CountryRepository countryRepository) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.countryRepository = countryRepository;

		logger.info("AuthenticationService initialized.");
	}

	public User signup(SignupUserDto signupUserDto) {
		logger.info("Starting signup process for email: {}", signupUserDto.getEmail());

		 Country country = countryRepository.findById(signupUserDto.getCountryId().toString())
		            .orElseThrow(() -> new IllegalArgumentException("Invalid phone extension: " + signupUserDto.getCountryId()));

		    var user = new User()
		            .setFullName(signupUserDto.getFullName())
		            .setEmail(signupUserDto.getEmail())
		            .setPassword(passwordEncoder.encode(signupUserDto.getPassword()))
		            .setAge(signupUserDto.getAge())
		            .setGender(signupUserDto.getGender())
		            .setPhone(signupUserDto.getPhone())
		            .setPlace(signupUserDto.getPlace())
		            .setCountryId(country) 
		            .setStatus(User.UserStatus.ACTIVE);

		List<Role> roles = new ArrayList<>();

		if (signupUserDto.getRoleId() != null) {
			logger.debug("Looking up role by ID: {}", signupUserDto.getRoleId());

			Role role = roleRepository.findById(signupUserDto.getRoleId()).orElseThrow(() -> {
				logger.error("Role not found with ID: {}", signupUserDto.getRoleId());
				return new RuntimeException("Role not found with ID: " + signupUserDto.getRoleId());
			});
			roles.add(role);
		} else {
			logger.debug("Assigning default role: ROLE_USER");
			Role defaultRole = roleRepository.findByRoleName("ROLE_USER").orElseThrow(() -> {
				logger.error("Default role 'USER' not found in the database.");
				return new RuntimeException("Default role 'USER' not found");
			});
			roles.add(defaultRole);
		}

		user.setRoles(roles);
		user = userRepository.save(user);
		logger.info("User successfully signed up with email: {}", user.getEmail());
		return user;
	}

	public User authenticate(SigninUserDto signinUserDto) {
		String identifier = signinUserDto.getIdentifier();
		String password = signinUserDto.getPassword();

		if (identifier == null || signinUserDto.getPassword() == null) {
			logger.warn("Email/Phone or password is null.");
			throw new UserIllegalArgumentException("Email/Phone number or password cannot be null", "Fill all Fields",
					400);
		}

		logger.info("Attempting authentication for identifier: {}", identifier);
		Optional<User> optionalUser;

		if (isPhone(identifier)) {
			logger.debug("Identifier '{}' is a phone number.", identifier);
			optionalUser = userRepository.findByPhone(identifier);
		} else if (isEmail(identifier)) {
			logger.debug("Identifier '{}' is an email address.", identifier);
			optionalUser = userRepository.findByEmail(identifier);
		} else {
			logger.error("Invalid format: {}", identifier);
			throw new UserIllegalArgumentException("Must be a valid email or phone number", "Check The Format", 400);
		}

		User user = optionalUser
				.orElseThrow(() -> new UserNotFoundException("Please register to login", "User not found", 500));

		try {
			logger.debug("Authenticating user with email: {}", user.getEmail());
			
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), password));
			
			logger.info("Authentication successful for user with email: {}", user.getEmail());
		} catch (Exception  ex) {
			
			logger.error("Incorrect email/phone or password user with email: {}", user.getEmail());
			
			throw new UserBadCredential("Incorrect email/phone or password", "Verify the given data", 401);
		}

		return user;
	}

	public List<User> allUsers() {
		logger.info("Fetching all users.");
		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);
		logger.info("Found {} users.", users.size());
		return users;
	}

	public boolean existsByPhone(String phone) {
		logger.debug("Checking if user exists with phone: {}", phone);
		return userRepository.existsByPhone(phone);
	}
	public boolean existsByEmail(String email) {
		logger.debug("Checking if user exists with phone: {}", email);
		return userRepository.existsByEmail(email);
	}

	private boolean isEmail(String input) {
		boolean result = input.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
		logger.debug("Input '{}' is {}a valid email.", input, result ? "" : "not ");
		return result;
	}

	private boolean isPhone(String input) {
		boolean result = input.matches("^\\+?[1-9]\\d{1,14}$");
		logger.debug("Input '{}' is {}a valid phone number.", input, result ? "" : "not ");
		return result;
	}

}
