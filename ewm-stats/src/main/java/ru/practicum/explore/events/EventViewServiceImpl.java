package ru.practicum.explore.events;


import io.micrometer.core.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class EventViewServiceImpl implements EventViewService {
    private EventViewStorage viewStorage;

    public EventViewServiceImpl(EventViewStorage viewStorage) {
        this.viewStorage = viewStorage;
    }

    @Override
    @Transactional
    public Map<Long, Integer> addEventView(List<Long> eventId, String httpAddress, String ipAddress) {
        List<EventView> events = viewStorage.addEventView(eventId, httpAddress, ipAddress);
        HashMap<Long, Integer> eventsViews = new HashMap<>();
        events.stream().forEach(event -> eventsViews.put(event.getEventId(), event.getView()));
        return eventsViews;
    }

    @Override
    public Integer getEventViewById(Long eventId, @Nullable String httpAddress, String ipAddress) {
        List<Integer> result = viewStorage.getEventViewById(eventId, httpAddress, ipAddress);
        if (result.size() == 0) {
            return 0;
        } else {
            return result.get(0);
        }
    }
}
