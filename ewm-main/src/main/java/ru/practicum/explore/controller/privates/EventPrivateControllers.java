package ru.practicum.explore.controller.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.NewEventDto;
import ru.practicum.explore.model.event.UpdateEventRequestDto;
import ru.practicum.explore.model.event.participation.ParticipationRequestDto;
import ru.practicum.explore.service.event.EventService;
import ru.practicum.explore.service.event.participation.ParticipationService;

import javax.validation.constraints.Positive;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateControllers {
    private final EventService eventService;
    private final ParticipationService participationService;

    @PostMapping
    public Event createEvent(@PathVariable Long userId,
                             @RequestBody NewEventDto eventDto) {
        if (eventDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Событие не может быть пустым");
        }
        try {
            return eventService.createEvent(userId, eventDto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public List<Event> findEventByUserId(@PathVariable @Positive Long userId,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findEventByUserId(userId, from, size);
    }

    @PatchMapping
    public Event updateEvent(@PathVariable @Positive Long userId,
                             @RequestBody UpdateEventRequestDto eventDto) {
        if (eventDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Событие не может быть пустым");
        }
        return eventService.updateEventByUserId(userId, eventDto);
    }

    @GetMapping("/{eventId}")
    public Event findEventById(@PathVariable Long userId,
                               @PathVariable Long eventId) {
        return eventService.findEventByEventIdAndUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public Event cancelEventById(@PathVariable Long userId,
                                 @PathVariable Long eventId) {
        return eventService.cancelEventBeforeModeration(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> findParticipationRequest(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        return participationService.findParticipationByEventIdAndUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmParticipation(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @PathVariable Long reqId) {
        return participationService.confirmParticipation(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto rejectParticipation(@PathVariable Long userId,
                                                       @PathVariable Long eventId,
                                                       @PathVariable Long reqId) {
        return participationService.rejectParticipation(userId, eventId, reqId);
    }
}
