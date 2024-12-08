package com.shiel.campaignapi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.exception.UserNotFoundException;
import com.shiel.campaignapi.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder ) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<SignupUserDto> allUsers() {
		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);
		List<SignupUserDto> userDtoList = new ArrayList<>();

		for (User user : users) {
			SignupUserDto signupDto = new SignupUserDto();
			signupDto.setFullName(user.getFullName());
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

	}

	public User updateUser(SignupUserDto userDto) {
		try {
			Optional<User> optionalUser = userRepository.findById(userDto.getUserId());

			if (optionalUser.isPresent()) {
				User user = optionalUser.get();

				user.setPlace(userDto.getPlace());
				user.setEmail(userDto.getEmail());
				user.setFullName(userDto.getFullName());
				user.setPhone(userDto.getPhone());
				user.setAge(userDto.getAge());
				user.setGender(userDto.getGender());
				user.setEmail(userDto.getEmail());
				user.setPassword(passwordEncoder.encode(userDto.getPassword()));
				user.setRoles(userDto.getRoles());

				return userRepository.save(user);
			} else {	
				throw new RuntimeException("User not found with id " + userDto.getUserId());
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public SignupUserDto findUserById(Integer userId) {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("No user found with ID: " + userId));

	    return mapUserToDto(user);
	}
	
	
	 private SignupUserDto mapUserToDto(User user) {
	        SignupUserDto dto = new SignupUserDto();
	        dto.setUserId(user.getUserId());
	        dto.setFullName(user.getFullName());
	        dto.setEmail(user.getEmail());
	        dto.setPhone(user.getPhone());
	        dto.setAge(user.getAge());
	        dto.setGender(user.getGender());
	        dto.setPlace(user.getPlace());
	        dto.setRoles(user.getRoles());
	        return dto;
	    }

	
}
