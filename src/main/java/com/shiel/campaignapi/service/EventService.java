package com.shiel.campaignapi.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.shiel.campaignapi.dto.EventDto;
import com.shiel.campaignapi.entity.Event;
import com.shiel.campaignapi.repository.EventRepository;

@Service
public class EventService {

	private final EventRepository eventRepository;

	public EventService(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	public String saveEvent(EventDto eventDto) {

		Event event = new Event();
		// event.setEventId(UUID.randomUUID().toString());
		event.setTitle(eventDto.getTitle());
		event.setDescription(eventDto.getDescription());
		event.setStartDate(eventDto.getStartDate());
		event.setEndDate(eventDto.getEndDate());
		event.setPlace(eventDto.getPlace());
		event.setAdultAmount(eventDto.getAdultAmount());
		event.setChildAmount(eventDto.getChildAmount());
		event.setSeats(eventDto.getSeats());
		event.setSeatsBooked(eventDto.getSeatsBooked());
		event.setStatus(Event.EventStatus.CREATED);

		Event save = eventRepository.save(event);
		return save.getEventId().toString();
	}

	public EventDto findEventById(String eventId) {
		return eventRepository.findById(eventId).map(this::mapToEventDto).orElse(null);
	}


	public List<EventDto> findAllEvents() {
		List<Event> events = eventRepository.findAll();
		return events.stream().map((event) -> mapToEventDto(event)).collect(Collectors.toList());

	}

	public EventDto updateEvent(EventDto eventDto) {
		try {
			Optional<Event> optionalEvent = eventRepository.findById(eventDto.getEventId().toString());
			if (optionalEvent.isPresent()) {
				Event event = optionalEvent.get();
				event.setTitle(eventDto.getTitle());
				event.setDescription(eventDto.getDescription());
				event.setPlace(eventDto.getPlace());
				event.setAdultAmount(eventDto.getAdultAmount());
				event.setChildAmount(eventDto.getChildAmount());
				event.setSeats(eventDto.getSeats());
				event.setSeatsBooked(eventDto.getSeatsBooked());
				event.setStatus(eventDto.getStatus());

				eventRepository.save(event);
				return eventDto;
			}
		} catch (Exception e) {
			return null;
		}
		return null;

	}

	public EventDto deleteEventById(String eventId) {
		Optional<Event> optionalEvent = eventRepository.findById(eventId);
		if (optionalEvent.isPresent()) {
			Event event = optionalEvent.get();
			eventRepository.delete(event);
			return mapToEventDto(event);
		} else {
			return null;
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

		return eventDto;

	}

	public boolean existsByTitle(String title) {
		return eventRepository.existsByTitle(title);
	}

	public boolean isValidDate(Date startDate, Date endDate) {
		return startDate != null && endDate != null && startDate.before(endDate) && startDate.after(new Date());
	}
}