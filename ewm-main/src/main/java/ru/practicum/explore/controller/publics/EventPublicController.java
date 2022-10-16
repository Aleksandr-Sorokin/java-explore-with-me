package ru.practicum.explore.controller.publics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.EventShortDto;
import ru.practicum.explore.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
@Slf4j
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> findFilterEvent(@RequestParam String text,
                                               @RequestParam List<Long> categories,
                                               @RequestParam Boolean paid,
                                               @RequestParam String rangeStart,
                                               @RequestParam String rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam String sort,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        return eventService.findFilterEvent(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public Event findEventById(@PathVariable Long id, HttpServletRequest request) {
        return eventService.findEventByIdPublished(id, request);
    }


}
