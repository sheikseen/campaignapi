package com.shiel.campaignapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shiel.campaignapi.entity.Meeting;

public interface MeetingRepository  extends JpaRepository<Meeting, String>{
boolean existsByTitle(String title);

}
