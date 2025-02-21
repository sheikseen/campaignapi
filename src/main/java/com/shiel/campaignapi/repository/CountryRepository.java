package com.shiel.campaignapi.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shiel.campaignapi.entity.Country;

public interface CountryRepository extends JpaRepository<Country,String>{
	 boolean existsByCountry(String country);

	Optional<Country> findByCountryCode(String countryCode);
}
