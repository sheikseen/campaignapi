package com.shiel.campaignapi.service;

import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> allUsers() {
		List<User> users = new ArrayList<>();

		userRepository.findAll().forEach(users::add);

		return users;
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
				user.setPassword(userDto.getPassword());

				return userRepository.save(user); 
			} else {
				throw new RuntimeException("User not found with id " + userDto.getUserId());
			}
		} catch (Exception e) {
			return null;
		}
	}

	
}
