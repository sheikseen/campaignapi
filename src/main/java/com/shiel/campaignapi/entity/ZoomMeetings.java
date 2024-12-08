package com.shiel.campaignapi.entity;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zoom_meetings")
public class ZoomMeetings implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "meetingid", nullable = false)
	private Integer meetingId;

	@Column(name = "place", nullable = false)
	private String place;

	@Column(name = "description", nullable = true)
	private String description;

	@Column(name = "day", nullable = false)
	private String day;

	@Column(name = "zoomid", nullable = false)
	private String zoomId;

	@Column(name = "zoomlink", nullable = false)
	private String zoomLink;

	@Column(name = "time", nullable = false)
	private LocalTime time;

	@CreationTimestamp
	@Column(updatable = false, name = "createdat")
	private Date createdAt;

	@UpdateTimestamp
	@Column(name = "updatedat")
	private Date updatedAt;

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ZoomMeetings() {

	}

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



	public ZoomMeetings(Integer meetingId, String place, String description, String day, String zoomId, String zoomLink,
			LocalTime time, Date createdAt, Date updatedAt) {
		super();
		this.meetingId = meetingId;
		this.place = place;
		this.description = description;
		this.day = day;
		this.zoomId = zoomId;
		this.zoomLink = zoomLink;
		this.time = time;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

}
