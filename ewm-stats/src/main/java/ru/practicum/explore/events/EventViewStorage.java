package ru.practicum.explore.events;

import io.micrometer.core.lang.Nullable;

import java.util.List;

public interface EventViewStorage {
    List<EventView> addEventView(List<Long> eventId, String httpAddress, String ipAddress);

    List<Integer> getEventViewById(Long eventId, @Nullable String httpAddress, String ipAddress);
}
