package com.shiel.campaignapi.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "meeting")
@Entity
public class Meeting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "meetingid", nullable = false)
	private Long meetingId;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "place", nullable = false)
	private String place;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "day", nullable = false)
	private String day;

	public Long getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Long meetingId) {
		this.meetingId = meetingId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Meeting(Long meetingId, String title, String place, String description, String day) {
		
		this.meetingId = meetingId;
		this.title = title;
		this.place = place;
		this.description = description;
		this.day = day;
	}

	public Meeting() {
		// TODO Auto-generated constructor stub
	}

}
