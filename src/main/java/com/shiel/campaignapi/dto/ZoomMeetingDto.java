package com.shiel.campaignapi.dto;

import java.time.LocalTime;

public class ZoomMeetingDto {
	private Integer meetingId;
	private String place;
	private String description;
	private String day;
	private String zoomId;
	private String zoomLink;
	private LocalTime time;
	public Integer getMeetingId() {
		return meetingId;
	}
	public void setMeetingId(Integer meetingId) {
		this.meetingId = meetingId;
	}
	
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getZoomId() {
		return zoomId;
	}
	public void setZoomId(String zoomId) {
		this.zoomId = zoomId;
	}
	public String getZoomLink() {
		return zoomLink;
	}
	public void setZoomLink(String zoomLink) {
		this.zoomLink = zoomLink;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public ZoomMeetingDto() {
		
	}
	
	
}
