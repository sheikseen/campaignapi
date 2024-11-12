package com.shiel.campaignapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shiel.campaignapi.entity.Role;

public interface RoleRepository extends JpaRepository<Role,String>{
	   Optional<Role> findByRoleName(String roleName);
}
