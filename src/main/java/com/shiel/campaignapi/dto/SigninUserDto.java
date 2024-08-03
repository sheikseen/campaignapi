package com.shiel.campaignapi.dto;

public class SigninUserDto {
	private String email;
	private String password;

	public String getEmail() {
		return email;
	}

	public SigninUserDto setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public SigninUserDto setPassword(String password) {
		this.password = password;
		return this;
	}

	@Override
	public String toString() {
		return "SigninUserDto{" + "email='" + email + '\'' + ", password='" + password + '\'' + '}';
	}
}
