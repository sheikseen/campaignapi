package com.shiel.campaignapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiel.campaignapi.dto.RoleDto;
import com.shiel.campaignapi.entity.Role;
import com.shiel.campaignapi.service.RoleService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/roles")
public class RoleController {

	@Autowired
	RoleService roleService;

	
	@PostMapping("/add")
	public ResponseEntity<?> addRole(@RequestBody RoleDto roleDto) {
		//if (roleService.existsByTitle(roleDto.getRoleName())) {
		//	return ResponseEntity.badRequest().body("Error: Role already Exist");
		//}
		Role roleId = roleService.saveRole(roleDto);
		String message = String.format("role registered successfully!",roleId);
		return ResponseEntity.ok(message);

	}

	//@PreAuthorize("hasAnyRole('APPLICATION', 'USER', 'ADMIN')")
	@GetMapping("/all")
	public ResponseEntity<?> getAllRole() {
		List<RoleDto> roles = roleService.findAllRoles();
		if (roles.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No roles Found");
		} else {
			return ResponseEntity.ok().body(roles);
		}
	}

	
	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateRole(@PathVariable("id") Long roleId,
			@Valid @RequestBody RoleDto roleDto) {

		if (roleId == null || roleDto == null) {
			return ResponseEntity.badRequest().body("role Id cannot be null");
		}
		if (!roleId.equals(roleDto.getRoleId())) {
			return ResponseEntity.badRequest().body("Invalid role ID in the request");

		}

		Role updatedRole = roleService.updateRole(roleDto);
		if (updatedRole == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("role Not found");
		} else {
			return ResponseEntity.ok().body(updatedRole);
		}

	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteRoleById(@PathVariable("id") @Valid Long roleId) {

		if (roleId == null) {
			return ResponseEntity.badRequest().body("role Id cannot be Null");
		}
		RoleDto role = roleService.deleteRoleById(roleId);
		if (role == null) {
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("role not found with ID: " + roleId);
		}
		return ResponseEntity.ok().body("role deleted successfully");

	}

}
