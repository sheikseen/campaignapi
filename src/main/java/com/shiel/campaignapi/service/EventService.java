package com.shiel.campaignapi.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.shiel.campaignapi.dto.EventDto;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.repository.EventRepository;

@Service
public class EventService {

	private final EventRepository eventRepository;
	private static final Logger logger = LoggerFactory.getLogger(EventService.class);

	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	public String saveEvent(EventDto eventDto) {
		logger.info("Saving event: {}", eventDto);
		try {

			Event event = new Event();
			event.setTitle(eventDto.getTitle());
			event.setDescription(eventDto.getDescription());
			event.setStartDate(eventDto.getStartDate());
			event.setEndDate(eventDto.getEndDate());
			event.setPlace(eventDto.getPlace());
			event.setAdultAmount(eventDto.getAdultAmount());
			event.setChildAmount(eventDto.getChildAmount());
			event.setSeats(eventDto.getSeats());
			event.setStatus(Event.EventStatus.CREATED);
			event.setMap(eventDto.getMap());
			event.setImage(eventDto.getImage());
			Event save = eventRepository.save(event);
			return save.getEventId().toString();
		} catch (Exception e) {
			logger.error("Error saving event: {}", eventDto, e);
			throw new RuntimeException("Error saving event", e);
		}
	}

	public EventDto findEventById(String eventId) {
		logger.info("Finding event by ID: {}", eventId);
		try {
			return eventRepository.findById(eventId).map(this::mapToEventDto).orElse(null);
		} catch (Exception e) {
			logger.error("Error finding event by ID: {}", eventId, e);
			throw new RuntimeException("Error finding event", e);
		}
	}

	public List<EventDto> findAllEvents() {
		logger.info("Finding all events");
		try {
			List<Event> events = eventRepository.findAll();
			return events.stream().map((event) -> mapToEventDto(event)).collect(Collectors.toList());
		} catch (Exception e) {
			logger.error("Error finding all events", e);
			throw new RuntimeException("Error finding events", e);
		}
	}

	public Event updateEvent(EventDto eventDto) {
		try {
			logger.info("Updating event: {}", eventDto);
			Optional<Event> optionalEvent = eventRepository.findById(eventDto.getEventId().toString());
			if (optionalEvent.isPresent()) {
				Event event = optionalEvent.get();

				event.setTitle(eventDto.getTitle());
				event.setDescription(eventDto.getDescription());
				event.setPlace(eventDto.getPlace());
				event.setStartDate(eventDto.getStartDate());
				event.setEndDate(eventDto.getEndDate());
				event.setAdultAmount(eventDto.getAdultAmount());
				event.setChildAmount(eventDto.getChildAmount());
				event.setSeats(eventDto.getSeats());
				event.setStatus(eventDto.getStatus());
				event.setMap(eventDto.getMap());
				event.setImage(eventDto.getImage());
				return eventRepository.save(event);
			} else {
				logger.warn("Event not found with ID: {}", eventDto.getEventId());
				throw new RuntimeException("Event not found with id " + eventDto.getEventId());
			}
		} catch (Exception e) {
			logger.error("Error updating event: {}", eventDto, e);
			throw new RuntimeException("Error updating event", e);
		}

	}

	public EventDto deleteEventById(Long eventId) {
		logger.info("Deleting event by ID: {}", eventId);
		try {
			Optional<Event> optionalEvent = eventRepository.findById(eventId.toString());
			if (optionalEvent.isPresent()) {
				Event event = optionalEvent.get();
				eventRepository.delete(event);
				return mapToEventDto(event);
			} else {
				logger.warn("Event not found for deletion with ID: {}", eventId);
			}
			return null;
		} catch (Exception e) {
			logger.error("Error deleting event by ID: {}", eventId, e);
			throw new RuntimeException("Error deleting event", e);
		}

	}

	private EventDto mapToEventDto(Event event) {
		EventDto eventDto = new EventDto();
		eventDto.setEventId(event.getEventId());
		eventDto.setTitle(event.getTitle());
		eventDto.setDescription(event.getDescription());
		eventDto.setPlace(event.getPlace());
		eventDto.setChildAmount(event.getChildAmount());
		eventDto.setAdultAmount(event.getAdultAmount());
		eventDto.setStartDate(event.getStartDate());
		eventDto.setEndDate(event.getEndDate());
		eventDto.setSeats(event.getSeats());
		eventDto.setSeatsBooked(event.getSeatsBooked());
		eventDto.setStatus(event.getStatus());
		eventDto.setMap(event.getMap());
		eventDto.setImage(event.getImage());
		return eventDto;

	}

	public boolean existsByTitle(String title) {
		logger.info("Checking if event exists by title: {}", title);
		try {
			return eventRepository.existsByTitle(title);
		} catch (Exception e) {
			logger.error("Error checking existence of event by title: {}", title, e);
			throw new RuntimeException("Error checking event existence", e);
		}
	}

	public boolean isValidDate(Date startDate, Date endDate) {
		logger.info("Validating dates: startDate={}, endDate={}", startDate, endDate);
		try {
			Date today = new Date();
			return startDate != null && endDate != null && startDate.before(endDate) && startDate.after(today)
					&& endDate.after(today);
		} catch (Exception e) {
			logger.error("Error validating dates: startDate={}, endDate={}", startDate, endDate, e);
			throw new RuntimeException("Error validating dates", e);
		}
	}
}