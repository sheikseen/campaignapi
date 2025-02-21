package com.shiel.campaignapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.CountryDto;
import com.shiel.campaignapi.entity.Country;
import com.shiel.campaignapi.service.CountryService;
@RestController
@RequestMapping("/country")
public class CountryController {

	@Autowired
	private CountryService countryService;
	@PostMapping("/add")
	public ResponseEntity<?> addMeeting(@RequestBody CountryDto countryDto) {
		if (countryService.existsByCountry(countryDto.getCountry())) {
			return ResponseEntity.badRequest().body("Error: Title already exists");
		}
		Country country = countryService.saveCountry(countryDto);
		return ResponseEntity.ok("Counry Added successfully with ID: " + country.getCountryId());
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllCountry() {
		List<Country> country =countryService.findAllCountry();
		if (country.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No country found");
		}
		return ResponseEntity.ok(country);
	}
	
	
}
