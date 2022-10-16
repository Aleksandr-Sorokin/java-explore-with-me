package ru.practicum.explore.events;

import io.micrometer.core.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface EventViewService {
    Map<Long, Integer> addEventView(List<Long> eventId, String httpAddress, String ipAddress);

    Integer getEventViewById(Long eventId, @Nullable String httpAddress, String ipAddress);
}
