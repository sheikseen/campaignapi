package com.shiel.campaignapi.dto;



public class RoleDto {

	private Long roleId;
	private String roleName;
	private String description;

	public RoleDto() {
	}

	public RoleDto(Long roleId, String roleName, String description ) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.description = description;

	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
