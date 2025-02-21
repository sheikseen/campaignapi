package com.shiel.campaignapi.exception;

public class BookingIllegalArgument extends RuntimeException  {
	private final String title;
	private final int status;
	private static final long serialVersionUID = 1L;
	
	public BookingIllegalArgument(String message , String title, int status) {
		super(message);
		this.title = title;
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public int getStatus() {
		return status;
	}
}
