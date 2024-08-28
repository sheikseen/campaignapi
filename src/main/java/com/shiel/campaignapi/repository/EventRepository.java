package com.shiel.campaignapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shiel.campaignapi.entity.Event;

public interface EventRepository extends JpaRepository<Event, String> {

	boolean existsByTitle(String title);

	boolean existsById(String id);

}
