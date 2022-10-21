package ru.practicum.explore.events.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.events.model.EndpointHit;
import ru.practicum.explore.events.model.ViewStats;
import ru.practicum.explore.events.storage.EventViewStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventViewServiceImpl implements EventViewService {
    private EventViewStorage viewStorage;

    public EventViewServiceImpl(EventViewStorage viewStorage) {
        this.viewStorage = viewStorage;
    }

    @Override
    @Transactional
    public void addEventView(EndpointHit endpointHit) {
        viewStorage.addEventView(endpointHit);
    }

    @Override
    public List<ViewStats> getAppView(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStats> result = viewStorage.getAppView(start, end, uris, unique);
        if (result.size() == 0) {
            return new ArrayList<>();
        } else {
            return result;
        }
    }
}
