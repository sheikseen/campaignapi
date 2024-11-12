package com.shiel.campaignapi.dto;

import java.util.List;

public class UserRoleDto {

	private Long id;
	private Long userId;
	private Long roleId;

	// Constructors
	public UserRoleDto() {
	}

	public UserRoleDto(Long id, Long userId, Long roleId) {
		this.id = id;
		this.userId = userId;
		this.roleId = roleId;

	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public List<UserRoleDto> userRole;

	public List<UserRoleDto> getUserRole() {
		return userRole;
	}

	public void setUserRole(List<UserRoleDto> userRole) {
		this.userRole = userRole;
	}

}
