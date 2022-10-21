package ru.practicum.explore.events.service;

import ru.practicum.explore.events.model.EndpointHit;
import ru.practicum.explore.events.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EventViewService {
    void addEventView(EndpointHit endpointHit);

    List<ViewStats> getAppView(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
