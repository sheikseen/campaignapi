package com.shiel.campaignapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.DependentDto;
import com.shiel.campaignapi.entity.Dependent;
import com.shiel.campaignapi.service.DependentsService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/dependent")
public class DependentsController {
	
	@Autowired
	DependentsService dependentsService;
	
	@PostMapping("/update/{dependentId}")
	public ResponseEntity<?> updateDependent(@PathVariable("dependentId") Long dependentId,
			@Valid @RequestBody DependentDto dependentDto) {

		if (dependentId == null || dependentDto == null) {
			return ResponseEntity.badRequest().body("Dependent Id cannot be null");
		}
		if (!dependentId.equals(dependentDto.getDependentId())) {
			return ResponseEntity.badRequest().body("Invalid dependent ID in the request");

		}

		Dependent updatedDependent = dependentsService.updateDependent(dependentDto);
		if (updatedDependent == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dependent Not found");
		} else {
			return ResponseEntity.ok().body(updatedDependent);
		}

	}

	@DeleteMapping("/delete/{dependentId}")
	public ResponseEntity<?> deleteDependentById(@PathVariable("dependentId") @Valid Long dependentId) {

		if (dependentId == null) {
			return ResponseEntity.badRequest().body("dependent Id cannot be Null");
		}
		DependentDto dependentDto = dependentsService.deleteDependentById(dependentId);
		if (dependentDto == null) {
			return 	ResponseEntity.status(HttpStatus.NOT_FOUND).body("Dependent not found with ID: " + dependentId);
		}
		return ResponseEntity.ok().body("Dependent deleted successfully");

	}
}
