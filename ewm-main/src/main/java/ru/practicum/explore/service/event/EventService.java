package ru.practicum.explore.service.event;

import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.event.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;

public interface EventService {
    List<Event> findEventByUserId(Long userId, Integer from, Integer size);

    Event updateEventByUserId(Long userId, UpdateEventRequestDto eventDto);

    Event createEvent(Long userId, NewEventDto eventDto) throws SQLException;

    Event findEventByEventIdAndUserId(Long userId, Long eventId);

    Event cancelEventBeforeModeration(Long userId, Long eventId);

    List<Event> searchEvent(List<Long> users, List<State> states, List<Long> categories,
                            String rangeStart, String rangeEnd, Integer from, Integer size);

    Event editEvent(Long eventId, AdminUpdateEventRequest eventDto) throws SQLException;

    Event publishEvent(Long eventId) throws SQLException;

    Event rejectEvent(Long eventId);

    List<EventShortDto> findFilterEvent(String text, List<Long> categories, Boolean paid,
                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                        String sort, Integer from, Integer size, HttpServletRequest request);

    Event findEventById(Long eventId);

    Event findEventByIdPublished(Long eventId, HttpServletRequest request);
}
