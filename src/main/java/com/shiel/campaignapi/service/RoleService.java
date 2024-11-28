package com.shiel.campaignapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.RoleDto;
import com.shiel.campaignapi.entity.Role;
import com.shiel.campaignapi.repository.RoleRepository;

import jakarta.validation.Valid;

@Service
public class RoleService {
	private final RoleRepository roleRepository;

	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public Role saveRole(RoleDto roleDto) {
		Role role = new Role();
		role.setRoleName(roleDto.getRoleName());
		role.setDescription(roleDto.getDescription());

		return	roleRepository.save(role);
		
	}

	public RoleDto findRoleById(@Valid String eventId) {
		return null;
	}

	public List<RoleDto> findAllRoles() {
		List<Role> roles = roleRepository.findAll();
		return roles.stream().map((role) -> mapToRoleDto(role)).collect(Collectors.toList());

	}

	public Role updateRole(RoleDto roleDto) {
		try {
			Optional<Role> roleOptional = roleRepository.findById(roleDto.getRoleId());
			if (roleOptional.isPresent()) {
				Role role = roleOptional.get();
				
				role.setRoleName(roleDto.getRoleName());
				role.setDescription(roleDto.getDescription());

				return roleRepository.save(role);
			} else {
				throw new RuntimeException("Meeting not found with id " + roleDto.getRoleId());
			}
		} catch (Exception e) {
			return null;
		}
	}

	public RoleDto deleteRoleById(@Valid Long roleId) {
		Optional<Role> optionalMeeting = roleRepository.findById(roleId);
		if (optionalMeeting.isPresent()) {
			Role role = optionalMeeting.get();
			roleRepository.delete(role);
			return mapToRoleDto(role);
		} else {
			return null;
		}
	}

	private RoleDto mapToRoleDto(Role role) {
		RoleDto roleDto = new RoleDto();
		roleDto.setRoleId(role.getRoleId());
		roleDto.setRoleName(role.getRoleName());
		roleDto.setDescription(role.getDescription());

		return roleDto;
	}
	
//	public boolean existsByTitle(String roleName) {
//		return roleRepository.existsByTitle(roleName);
//	}

}
