package com.shiel.campaignapi.dto;

import java.util.List;

import com.shiel.campaignapi.entity.Role;
import com.shiel.campaignapi.entity.User;

public class SignupUserDto {
	private Integer userId;
	private String fullName;
	private String place;
	private String phoneExt;
	private String phone;
	private int age;
	private String gender;
	private String email;
	private String password;
	private List<Role> roles;
	private Long roleId;
	private User.UserStatus status;

	public String getEmail() {
		return email;
	}

	public SignupUserDto setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public SignupUserDto setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public SignupUserDto setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getPhoneExt() {
		return phoneExt;
	}

	public void setPhoneExt(String phoneExt) {
		this.phoneExt = phoneExt;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public User.UserStatus getStatus() {
		return status;
	}

	public void setStatus(User.UserStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "SignupUserDto [userId=" + userId + ", fullName=" + fullName + ", place=" + place + ", phoneExt="
				+ phoneExt + " phone=" + phone + ", age=" + age + ", gender=" + gender + ", email=" + email
				+ ", password=" + password + ",roles=" + roles + ",roleId=" + roleId + ",status=" + status + "]";
	}

}
