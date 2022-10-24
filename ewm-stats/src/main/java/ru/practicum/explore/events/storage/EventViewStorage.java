package ru.practicum.explore.events.storage;

import ru.practicum.explore.events.model.EndpointHit;
import ru.practicum.explore.events.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EventViewStorage {
    void addEventView(EndpointHit endpointHit);

    List<ViewStats> getAppView(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
