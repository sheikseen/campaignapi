package com.shiel.campaignapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shiel.campaignapi.entity.ZoomMeetings;

public interface ZoomMeetingRepository extends JpaRepository<ZoomMeetings, String> {
	boolean existsByPlace(String place);
	   List<ZoomMeetings> findByPlace(String place); 

}
