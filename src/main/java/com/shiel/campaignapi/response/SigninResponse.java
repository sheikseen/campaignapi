package com.shiel.campaignapi.response;

public class SigninResponse {
	private String token;

	private long expiresIn;

	public String getToken() {
		return token;
	}

	public SigninResponse setToken(String token) {
		this.token = token;
		return this;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public SigninResponse setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
		return this;
	}

	@Override
	public String toString() {
		return "SigninResponse{" + "token='" + token + '\'' + ", expiresIn=" + expiresIn + '}';
	}
}