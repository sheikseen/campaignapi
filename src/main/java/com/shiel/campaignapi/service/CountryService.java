package com.shiel.campaignapi.service;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.CountryDto;
import com.shiel.campaignapi.entity.Country;
import com.shiel.campaignapi.repository.CountryRepository;


@Service
public class CountryService {
	private final CountryRepository countryRepository;
	private static final Logger logger = LoggerFactory.getLogger(ZoomMeetingService.class);

	public CountryService(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}

	public Country saveCountry(CountryDto countryDto) {
		logger.info("Saving Country ");
		try {
			Country country = new Country();
			country.setCountryCode(countryDto.getCountryCode());
			country.setCountry(countryDto.getCountry());

			return countryRepository.save(country);
		} catch (Exception e) {
			logger.error("Error occurred while Saving County", e);
			throw new RuntimeException("Failed to save Country", e);
		}
	}

	public List<Country> findAllCountry() {
		return countryRepository.findAll();
	}

	public boolean existsByCountry(String country) {
		return countryRepository.existsByCountry(country);
	}

}
