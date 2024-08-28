package com.shiel.campaignapi.dto;

public class SignupUserDto {
	private Integer userId;
	private String fullName;
	private String place;
	private String phone;
	private int age;
	private String gender;
	private String email;
	private String password;

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

	@Override
	public String toString() {
		return "SignupUserDto [userId=" + userId + ", fullName=" + fullName + ", place=" + place + ", phone=" + phone
				+ ", age=" + age + ", gender=" + gender + ", email=" + email + ", password=" + password + "]";
	}

}
