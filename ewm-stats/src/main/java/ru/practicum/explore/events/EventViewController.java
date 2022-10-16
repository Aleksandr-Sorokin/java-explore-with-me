package ru.practicum.explore.events;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/views")
public class EventViewController {
    private final EventViewService viewService;

    @PostMapping
    public Map<Long, Integer> addEventView(@RequestParam List<Long> eventId,
                                           @RequestParam(required = false) String httpAddress,
                                           @RequestParam(required = false) String ipAddress) {
        return viewService.addEventView(eventId, httpAddress, ipAddress);
    }

    @GetMapping("/{eventId}")
    public Integer getEventViewById(@PathVariable Long eventId,
                                    @RequestParam(required = false) String httpAddress,
                                    @RequestParam(required = false) String ipAddress) {
        return viewService.getEventViewById(eventId, httpAddress, ipAddress);
    }
}
