package com.shiel.campaignapi.dto;

public class SigninUserDto {
	private String identifier;
	private String password;

	public String getPassword() {
		return password;
	}

	public SigninUserDto setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getIdentifier() {
		return identifier;
	}

	public SigninUserDto setIdentifier(String identifier) {
		this.identifier = identifier;
		return this;
	}

	@Override
	public String toString() {
		return "SigninUserDto{" + "identifier='" + identifier + '\'' + ", password='" + password + '\'' + '}';
	}
}
