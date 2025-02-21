package com.shiel.campaignapi.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shiel.campaignapi.dto.DependentDto;
import com.shiel.campaignapi.entity.Booking;
import com.shiel.campaignapi.entity.Dependent;
import com.shiel.campaignapi.repository.BookingRepository;
import com.shiel.campaignapi.repository.DependentRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class DependentsService {
	private final DependentRepository dependentRepository;
	private final BookingRepository bookingRepository;

	public DependentsService(DependentRepository dependentRepository, BookingRepository bookingRepository) {
		this.dependentRepository = dependentRepository;
		this.bookingRepository = bookingRepository;
	}

	public Dependent updateDependent(DependentDto dependentDto) {
		try {
			Optional<Dependent> dependentOptional = dependentRepository.findById(dependentDto.getDependentId());
			if (dependentOptional.isPresent()) {
				Dependent dependent = dependentOptional.get();

				dependent.setName(dependentDto.getName());
				dependent.setPlace(dependentDto.getPlace());
				dependent.setGender(dependentDto.getGender());
				dependent.setAge(dependentDto.getAge());
				dependent.setRelation(dependentDto.getRelation());

				return dependentRepository.save(dependent);
			} else {
				throw new RuntimeException("Dependent not found with id " + dependentDto.getDependentId());
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional
	public DependentDto deleteDependentById(@Valid Long dependentId) {
		try {
			Dependent dependent = dependentRepository.findById(dependentId)
					.orElseThrow(() -> new RuntimeException("Dependent not found with ID: " + dependentId));

			Booking booking = dependent.getBookingId();

			if (booking != null) {

				booking.getDependents().remove(dependent);
				booking.setDependentCount(booking.getDependents().size());
				
				BigDecimal amountToSubtract = BigDecimal.ZERO;
				
				if (dependent.getAge() > 7 && dependent.getAge() < 12) {
					amountToSubtract = booking.getEventId().getChildAmount();
				} else if (dependent.getAge() >= 12) {
					amountToSubtract = booking.getEventId().getAdultAmount();
				}

				booking.setTotalAmount(booking.getTotalAmount().subtract(amountToSubtract));

				if (booking.getBookingStatus() == Booking.BookingStatus.DEPENDENT
						&& booking.getDependents().isEmpty()) {
					booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
				}

				bookingRepository.save(booking);
			}
			return mapToDependentDto(dependent);
		} catch (Exception e) {
			throw new RuntimeException("Error deleting dependent with ID: " + dependentId, e);
		}

	}

	private DependentDto mapToDependentDto(Dependent dependent) {
		DependentDto dependentDto = new DependentDto();
		dependentDto.setDependentId(dependent.getDependentId());
		dependentDto.setName(dependent.getName());
		dependentDto.setPlace(dependent.getPlace());
		dependentDto.setGender(dependent.getGender());
		dependentDto.setAge(dependent.getAge());
		dependentDto.setRelation(dependent.getRelation());

		return dependentDto;
	}

}
