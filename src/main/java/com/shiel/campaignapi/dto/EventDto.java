package com.shiel.campaignapi.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.shiel.campaignapi.entity.Event;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

public class EventDto implements Serializable {
 static final long serialVersionUID = 1L;
	private Long eventId;
	private String title;
	private String description;
	private String place;
	private int seats;

	@DecimalMin(value = "1.00", inclusive = true, message = "Amount must be greater than or equal to 1.00")
	@Digits(integer = 10, fraction = 2, message = "Amount has exceeded the allowed precision")
	private BigDecimal adultAmount;

	@DecimalMin(value = "1.00", inclusive = true, message = "Amount must be greater than or equal to 1.00")
	@Digits(integer = 10, fraction = 2, message = "Amount has exceeded the allowed precision")
	private BigDecimal childAmount;

	private Date startDate;
	private Date endDate;
	private Event.EventStatus status;
	private int seatsBooked;

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public BigDecimal getAdultAmount() {
		return adultAmount;
	}

	public void setAdultAmount(BigDecimal adultAmount) {
		this.adultAmount = adultAmount;
	}

	public BigDecimal getChildAmount() {
		return childAmount;
	}

	public void setChildAmount(BigDecimal childAmount) {
		this.childAmount = childAmount;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public int getSeatsBooked() {
		return seatsBooked;
	}

	public EventDto setSeatsBooked(int seatsBooked) {
		this.seatsBooked = seatsBooked;
		return this;
	}

	public Event.EventStatus getStatus() {
		return status;
	}

	public EventDto setStatus(Event.EventStatus status) {
		this.status = status;
		return this;
	}

	public EventDto() {
	}

}
