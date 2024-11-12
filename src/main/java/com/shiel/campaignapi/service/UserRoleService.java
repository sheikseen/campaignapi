package com.shiel.campaignapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.UserRoleDto;
import com.shiel.campaignapi.entity.Role;
import com.shiel.campaignapi.entity.User;
import com.shiel.campaignapi.entity.UserRole;
import com.shiel.campaignapi.repository.RoleRepository;
import com.shiel.campaignapi.repository.UserRepository;
import com.shiel.campaignapi.repository.UserRoleRepository;

@Service
public class UserRoleService {
	private final UserRoleRepository userRoleRepository;

	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;

	public UserRoleService(UserRoleRepository userRoleRepository) {
		this.userRoleRepository = userRoleRepository;

	}

	public UserRole saveUserRole(UserRoleDto userRoleDto) {

		UserRole userRole = new UserRole();

		User user = userRepository.findById(Integer.parseInt(userRoleDto.getUserId().toString()))
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + userRoleDto.getUserId()));
		userRole.setUserId(user);

		Role role = roleRepository.findById(userRoleDto.getRoleId().toString())
				.orElseThrow(() -> new RuntimeException("Role Not Found wih id" + userRoleDto.getRoleId()));
		
		UserRole savedUserRole = userRoleRepository.save(userRole);
		return savedUserRole;

	}

}
