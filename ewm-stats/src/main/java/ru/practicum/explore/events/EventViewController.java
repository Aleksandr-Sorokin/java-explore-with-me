package ru.practicum.explore.events;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.events.model.EndpointHit;
import ru.practicum.explore.events.model.ViewStats;
import ru.practicum.explore.events.service.EventViewService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class EventViewController {
    private final EventViewService viewService;

    @PostMapping("/hit")
    public void addEventView(@RequestBody @Validated EndpointHit endpointHit) {
        viewService.addEventView(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getAppView(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
                                      @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
                                      @RequestParam(required = false) List<String> uris,
                                      @RequestParam(defaultValue = "false") Boolean unique) {
        return viewService.getAppView(startDate, endDate, uris, unique);
    }
}
