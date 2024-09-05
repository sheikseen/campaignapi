package com.shiel.campaignapi.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "dependent")
@Entity
public class Dependent implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dependentid", nullable = false)
	private Long dependentId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "place", nullable = false)
	private String place;

	@Column(name = "age", nullable = false)
	private Integer age;

	@Column(name = "gender", nullable = false)
	private String gender;

	@Column(name = "relation", nullable = false)
	@Enumerated(EnumType.STRING)
	private Relation relation;

	@ManyToOne
	@JoinColumn(name = "userid", referencedColumnName = "userid")
	private User userId;

	@ManyToOne
	@JoinColumn(name = "bookingid", referencedColumnName = "bookingid", nullable = true)
	private Booking bookingId;

	public Long getDependentId() {
		return dependentId;
	}

	public void setDependentId(Long dependentId) {
		this.dependentId = dependentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public Booking getBookingId() {
		return bookingId;
	}

	public void setBookingId(Booking bookingId) {
		this.bookingId = bookingId;
	}

	public Dependent(Long dependentId, String name, String place, Integer age, String gender, User userId,
			Booking bookingId, Relation relation) {
		super();
		this.dependentId = dependentId;
		this.name = name;
		this.place = place;
		this.age = age;
		this.gender = gender;
		this.userId = userId;
		this.bookingId = bookingId;
		this.relation = relation;
	}

	public enum Relation {
		FATHER, MOTHER, WIFE, HUSBAND, DAUGHTER, SON, SISTER, BROTHER
	}

	public Dependent() {

	}
}
