package com.shiel.campaignapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	 List<Booking> findByEventId(Event eventId);
	 List<Booking> findByUserId(User userId);
	    Optional<Booking> findByUserIdAndEventId(User userId, Event eventId);

	    boolean existsByUserIdAndEventId(User userId, Event eventId);

}
