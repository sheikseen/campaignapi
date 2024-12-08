package com.shiel.campaignapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "country")
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "countryid", nullable = false)
	private int countryId;

	@Column(name = "countrycode", nullable = false)
	private String contryCode;

	@Column(name = "country", nullable = false)
	private String country;

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public String getContryCode() {
		return contryCode;
	}

	public void setContryCode(String contryCode) {
		this.contryCode = contryCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Country() {

	}

	@Override
	public String toString() {
		return "Country [countryId=" + countryId + ", contryCode=" + contryCode + ", country=" + country + "]";
	}

}
