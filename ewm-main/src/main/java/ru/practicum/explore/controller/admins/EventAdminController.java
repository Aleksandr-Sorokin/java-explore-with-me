package ru.practicum.explore.controller.admins;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.event.AdminUpdateEventRequest;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.service.event.EventService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {
    private final EventService eventService;

    @GetMapping
    public List<Event> searchEvent(@RequestParam List<Long> users,
                                   @RequestParam List<State> states,
                                   @RequestParam List<Long> categories,
                                   @RequestParam String rangeStart,
                                   @RequestParam String rangeEnd,
                                   @RequestParam(defaultValue = "0") Integer from,
                                   @RequestParam(defaultValue = "10") Integer size) {
        return eventService.searchEvent(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    public Event editEvent(@PathVariable @Positive Long eventId,
                           @RequestBody AdminUpdateEventRequest eventDto) {
        if (eventDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Событие не может быть пустым");
        }
        return eventService.editEvent(eventId, eventDto);
    }

    @PatchMapping("/{eventId}/publish")
    public Event publishEvent(@PathVariable @Positive Long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public Event rejectEvent(@PathVariable @Positive Long eventId) {
        return eventService.rejectEvent(eventId);
    }
}
