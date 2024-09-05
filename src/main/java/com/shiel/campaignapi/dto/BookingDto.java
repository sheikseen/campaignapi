package com.shiel.campaignapi.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Booking.PaymentMethod;

public class BookingDto {
	public Long bookingId;
	public int dependentCount;
	public Booking.PaymentMethod paymentVia;
	public BigDecimal totalAmount;
	public BigDecimal amountPaid;
	public boolean isPaid;
	public Booking.BookingStatus bookingStatus;
	public Date bookingDate;
	public Integer userId;
//	public Long dependentId;
	public Long eventId;
	private Integer loggedInUserId;

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public int getDependentCount() {
		return dependentCount;
	}

	public void setDependentCount(int dependentCount) {
		this.dependentCount = dependentCount;
	}

	public Booking.PaymentMethod getPaymentVia() {
		return paymentVia;
	}

	public void setPaymentVia(Booking.PaymentMethod paymentVia) {
		this.paymentVia = paymentVia;
	}

	public Booking.BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(Booking.BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}

	public Date getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(Date bookingDate) {
		this.bookingDate = bookingDate;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

//	public Long getDependentId() {
//		return dependentId;
//	}
//
//	public void setDependentId(Long dependentId) {
//		this.dependentId = dependentId;
//	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Integer getLoggedInUserId() {
		return loggedInUserId;
	}

	public void setLoggedInUserId(Integer loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}

	public List<DependentDto> dependents;

	public List<DependentDto> getDependents() {
		return dependents;
	}

	public void setDependents(List<DependentDto> dependents) {
		this.dependents = dependents;
		
	}

	public BookingDto() {

	}

}
