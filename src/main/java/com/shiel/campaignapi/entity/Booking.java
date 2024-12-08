package com.shiel.campaignapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Table(name = "booking")
@Entity
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bookingid")
	private Long bookingId;

	@Column(name = "dependentcount", nullable = true)
	private int dependentCount;

	@Column(name = "paymentvia")
	private PaymentMethod paymentVia;

	@Column(name = "totalamount")
	private BigDecimal totalAmount;

	@Column(name = "amountpaid")
	private BigDecimal amountPaid;

	@Column(name = "ispaid")
	private boolean isPaid;

	@Column(name = "bookingstatus")
	private BookingStatus bookingStatus;

	@Column(name = "bookingdate")
	private LocalDateTime bookingDate;

	@ManyToOne
	@JoinColumn(name = "userid", referencedColumnName = "userid")
	private User userId;

	@ManyToOne
	@JoinColumn(name = "eventid", referencedColumnName = "eventid", nullable = true)
	private Event eventId;

	@OneToMany(mappedBy = "bookingId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Dependent> dependents;

	@CreationTimestamp
	@Column(updatable = false, name = "createdat")
	private Date createdAt;

	@UpdateTimestamp
	@Column(name = "updatedat")
	private Date updatedAt;

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

	public PaymentMethod getPaymentVia() {
		return paymentVia;
	}

	public void setPaymentVia(PaymentMethod paymentVia) {
		this.paymentVia = paymentVia;
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

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime localDateTime) {
		this.bookingDate = localDateTime;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public Event getEventId() {
		return eventId;
	}

	public void setEventId(Event eventId) {
		this.eventId = eventId;
	}

	public List<Dependent> getDependents() {
		return dependents;
	}

	public void setDependents(List<Dependent> dependents) {
		this.dependents = dependents;
	}

	public enum PaymentMethod {
		CASH, GOOGLE_PAY, CARD
	}

	public enum BookingStatus {
		PENDING, CONFIRMED, CANCELLED, COMPLETED, REJECTED
	}

	public enum PaymentStatus {
		PENDING, PAID, REFUNDED, CANCELLED,
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public Booking setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Booking setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Booking() {
	}

	@Override
	public String toString() {
		return "Booking [bookingId=" + bookingId + ", dependentCount=" + dependentCount + ", paymentVia=" + paymentVia
				+ ", totalAmount=" + totalAmount + ", amountPaid=" + amountPaid + ", isPaid=" + isPaid
				+ ", bookingStatus=" + bookingStatus + ", bookingDate=" + bookingDate + ", userId=" + userId
				+ ", eventId=" + eventId + ",createdAt=" + createdAt + ",updatedat=" + updatedAt + "]";
	}

}
