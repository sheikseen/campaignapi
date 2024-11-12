package com.shiel.campaignapi.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.SigninUserDto;
import com.shiel.campaignapi.dto.SignupUserDto;
import com.shiel.campaignapi.entity.Role;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.entity.UserRole;
import com.shiel.campaignapi.repository.RoleRepository;
import com.shiel.campaignapi.repository.UserRepository;
import com.shiel.campaignapi.repository.UserRoleRepository;


@Service
public class AuthenticationService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager,
			PasswordEncoder passwordEncoder,RoleRepository roleRepository,UserRoleRepository userRoleRepository ) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository=roleRepository;
		this.userRoleRepository=userRoleRepository;
	}

	public User signup(SignupUserDto signupUserDto) {
		var user = new User().setFullName(signupUserDto.getFullName()).setEmail(signupUserDto.getEmail())
				.setPassword(passwordEncoder.encode(signupUserDto.getPassword())).setAge(signupUserDto.getAge())
				.setGender(signupUserDto.getGender()).setPhone(signupUserDto.getPhone())
				.setPlace(signupUserDto.getPlace());
		 user = userRepository.save(user);
		
		  Role defaultRole = roleRepository.findByRoleName("USER")
	                .orElseThrow(() -> new RuntimeException("Role not found"));
		

	        // Create a new UserRole entry
	        UserRole userRole = new UserRole();
	        userRole.setUserId(user);           
	        userRole.setRoleId(defaultRole);      
	        userRoleRepository.save(userRole);
	        return user;
	}

	public User authenticate(SigninUserDto signinUserDto) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signinUserDto.getEmail(), signinUserDto.getPassword()));
		return userRepository.findByEmail(signinUserDto.getEmail()).orElseThrow();
	}

	public List<User> allUsers() {
		List<User> users = new ArrayList<>();
		userRepository.findAll().forEach(users::add);
		return users;
	}
}
