package com.shiel.campaignapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.CountryDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.Country;
import com.shiel.campaignapi.entity.PasswordResetToken;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.exception.UserBadCredential;
import com.shiel.campaignapi.exception.UserIllegalArgumentException;
import com.shiel.campaignapi.exception.UserNotFoundException;
import com.shiel.campaignapi.repository.CountryRepository;
import com.shiel.campaignapi.repository.PasswordResetTokenRepository;
import com.shiel.campaignapi.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final CountryRepository countryRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private JavaMailSender mailSender;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			PasswordResetTokenRepository passwordResetTokenRepository, CountryRepository countryRepository) {
		this.userRepository = userRepository;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.countryRepository = countryRepository;
	}

	public List<SignupUserDto> allUsers() {
		logger.info("Fetching all users");
		try {
			List<User> users = new ArrayList<>();
			userRepository.findAll().forEach(users::add);
			List<SignupUserDto> userDtoList = new ArrayList<>();

			for (User user : users) {
				SignupUserDto signupDto = new SignupUserDto();
				signupDto.setFullName(user.getFullName());

				Country country = user.getCountryId();
				if (country != null) {
					CountryDto countryDto = new CountryDto();
					countryDto.setCountryId(country.getCountryId());
					countryDto.setCountryCode(country.getCountryCode());
					countryDto.setCountry(country.getCountry());
					signupDto.setCountry(countryDto);
				}
				signupDto.setPhone(user.getPhone());
				signupDto.setAge(user.getAge());
				signupDto.setEmail(user.getEmail());
				signupDto.setGender(user.getGender());
				signupDto.setPlace(user.getPlace());
				signupDto.setRoles(user.getRoles());
				signupDto.setUserId(user.getUserId());
				signupDto.setStatus(user.getStatus());
				userDtoList.add(signupDto);
			}

			return userDtoList;
		} catch (Exception e) {
			logger.error("Error occurred while fetching all users", e);
			throw new RuntimeException("Failed to fetch users", e);
		}

	}

	public User updateUser(SignupUserDto userDto) {
		logger.info("Updating user with ID: {}", userDto.getUserId());

		Optional<User> optionalUser = userRepository.findById(userDto.getUserId());

		if (optionalUser.isEmpty()) {
			throw new RuntimeException("User not found with id " + userDto.getUserId());
		}
		User user = optionalUser.get();

		user.setPlace(userDto.getPlace());
		user.setEmail(userDto.getEmail());
		user.setFullName(userDto.getFullName());

		if (userDto.getCountryId() != null) {
			Country country = countryRepository.findById(userDto.getCountryId().toString())
					.orElseThrow(() -> new RuntimeException("Country not found with ID: " + userDto.getCountryId()));
			user.setCountryId(country);
		}

		user.setPhone(userDto.getPhone());

		if (userDto.getAge() % 1 != 0) {
			throw new UserBadCredential("Decimal Values are not allowed", "Check the age given", 400);
		}
		user.setAge(userDto.getAge());

		user.setGender(userDto.getGender());

		if (userDto.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		}

		if (userDto.getRoles() != null) {
			user.setRoles(userDto.getRoles());
		}

		return userRepository.save(user);

	}

	public SignupUserDto findUserById(Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(
				() -> new UserNotFoundException("No user found with ID: " + userId, "Please register to login", 400));

		return mapUserToDto(user);
	}

	private SignupUserDto mapUserToDto(User user) {
		SignupUserDto dto = new SignupUserDto();
		dto.setUserId(user.getUserId());
		dto.setFullName(user.getFullName());
		dto.setEmail(user.getEmail());

		Country country = user.getCountryId();
		if (country != null) {
			CountryDto countryDto = new CountryDto();
			countryDto.setCountryId(country.getCountryId());
			countryDto.setCountryCode(country.getCountryCode());
			countryDto.setCountry(country.getCountry());
			dto.setCountry(countryDto);
		}
		dto.setPhone(user.getPhone());
		dto.setAge(user.getAge());
		dto.setGender(user.getGender());
		dto.setPlace(user.getPlace());
		dto.setRoles(user.getRoles());
		return dto;
	}

	public PasswordResetToken createPasswordResetToken(String email) {

		if (email == null || email.isEmpty()) {
			throw new UserIllegalArgumentException("Email must be provided", " Fill the field ", 400);
		}
		Optional<User> userOpt = userRepository.findByEmail(email);

		if (userOpt.isEmpty()) {
			throw new UserBadCredential("Invalid email address provided", "Verify the given data", 401);
		}

		User user = userOpt.get();
		String otp = generateOtp();
		LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10);
		PasswordResetToken resetToken = new PasswordResetToken(otp, user, expiryDate);
		passwordResetTokenRepository.save(resetToken);
		return resetToken;
	}

	public String generateOtp() {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public void sendUpdateEmail(String recipientEmail, String fullName, String updateType) {
		String subject = "Account Update Notification";
		String message = String.format("Dear %s,\n\n" + "Your account information has been successfully updated.\n"
				+ "Update Type: %s\n" + "Date and Time: %s\n\n"
				+ "If you did not request this update, please contact our support team immediately.\n\n"
				+ "Best Regards,\n" + "Shiel Bible Home", fullName, updateType, LocalDateTime.now());

		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientEmail);
		email.setSubject(subject);
		email.setText(message);

		mailSender.send(email);
	}

	public PasswordResetToken validatePasswordResetToken(String otp) {
		return passwordResetTokenRepository.findByToken(otp).filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
				.orElseThrow(
						() -> new UserBadCredential("Invalid or expired password reset token", "Incorect Tocken", 400));

	}

	public void invalidatePasswordResetToken(PasswordResetToken token) {
		passwordResetTokenRepository.delete(token);
	}

}
