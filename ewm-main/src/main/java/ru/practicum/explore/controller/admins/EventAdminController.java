package ru.practicum.explore.controller.admins;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.event.AdminUpdateEventRequest;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.service.event.EventService;

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
    public Event editEvent(@PathVariable Long eventId,
                           @RequestBody AdminUpdateEventRequest eventDto) {
        return eventService.editEvent(eventId, eventDto);
    }

    @PatchMapping("/{eventId}/publish")
    public Event publishEvent(@PathVariable Long eventId) {
        return eventService.publishEvent(eventId);
    }

    @PatchMapping("/{eventId}/reject")
    public Event rejectEvent(@PathVariable Long eventId) {
        return eventService.rejectEvent(eventId);
    }
}
