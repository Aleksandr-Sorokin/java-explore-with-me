package ru.practicum.explore.storage.location;

import ru.practicum.explore.model.location.Location;

public interface LocationStorage {
    Long createLocation(Location location);

    Location findLocation(Long id);
}
