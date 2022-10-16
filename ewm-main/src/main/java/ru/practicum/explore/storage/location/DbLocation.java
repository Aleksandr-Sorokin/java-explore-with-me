package ru.practicum.explore.storage.location;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.location.Location;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DbLocation implements LocationStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long createLocation(Location location) {
        try {
            Map<String, Object> keysLocation = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("locations")
                    .usingColumns("latitude", "longitude")
                    .usingGeneratedKeyColumns("location_id")
                    .executeAndReturnKeyHolder(Map.of(
                            "latitude", location.getLat(),
                            "longitude", location.getLon()))
                    .getKeys();
            return (Long) keysLocation.get("location_id");
        } catch (DuplicateKeyException e) {
            String sql = "SELECT location_id FROM locations WHERE latitude = ? AND longitude = ?;";
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql,
                    location.getLat(), location.getLon());
            if (rowSet.next()) {
                return rowSet.getLong("location_id");
            }
        }

        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("locations")
                .usingColumns("latitude", "longitude")
                .usingGeneratedKeyColumns("location_id")
                .executeAndReturnKeyHolder(Map.of(
                        "latitude", location.getLat(),
                        "longitude", location.getLon()))
                .getKeys();
        return (Long) keys.get("location_id");
    }

    @Override
    public Location findLocation(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(
                "SELECT * FROM locations WHERE location_id = ?", id);
        if (rowSet.next()) {
            Location location = new Location();
            location.setLon(rowSet.getFloat("longitude"));
            location.setLat(rowSet.getFloat("latitude"));
            return location;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Локация отсутствует");
        }
    }
}
