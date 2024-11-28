package com.shiel.campaignapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	 List<Booking> findByEventId(Event eventId);
	 List<Booking> findByUserId(User userId);

//	@Query("SELECT b FROM Booking b WHERE b.userId.userId = :userId AND b.userId.phone = :phone")
//	List<Booking> findByUserIdAndPhone(@Param("userId") Long userId, @Param("phone") String phone);
}
