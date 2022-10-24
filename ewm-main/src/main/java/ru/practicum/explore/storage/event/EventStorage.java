package ru.practicum.explore.storage.event;

import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.event.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface EventStorage {
    List<Event> findEventByUserId(Long userId, Integer from, Integer size);

    Event updateEventByUserId(Long userId, UpdateEventRequestDto eventDto);

    Event createEvent(Long userId, NewEventDto eventDto, Long idLocation) throws SQLException;

    Event findEventByEventIdAndUserId(Long userId, Long eventId);

    Event cancelEventBeforeModeration(Long userId, Long eventId);

    List<Event> searchEvent(List<Long> users, List<State> states, List<Long> categories,
                            String rangeStart, String rangeEnd, Integer from, Integer size);

    Event editEvent(Long eventId, AdminUpdateEventRequest eventDto) throws SQLException;

    Event publishEvent(Long eventId) throws SQLException;

    Event rejectEvent(Long eventId);

    List<EventShortDto> findFilterEvent(String text, List<Long> categories, Boolean paid,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                        String sort, Integer from, Integer size);

    Event findEventById(Long eventId);

    Event findEventByIdPublished(Long eventId);

    List<Event> findEventByIdCategory(Long categoryId);
}
