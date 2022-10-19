package ru.practicum.explore.storage.location;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.location.Location;

import java.sql.*;

@Component
@RequiredArgsConstructor
public class DbLocation implements LocationStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long createLocation(Location location) {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            String sqlForView = "INSERT INTO locations (latitude, longitude) VALUES (?, ?) " +
                    "ON CONFLICT (latitude, longitude) " +
                    "DO UPDATE SET latitude = excluded.latitude, longitude = excluded.longitude;";
            PreparedStatement statement = connection.prepareStatement(sqlForView, Statement.RETURN_GENERATED_KEYS);
            statement.setFloat(1, location.getLat());
            statement.setFloat(2, location.getLon());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getLong("location_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
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
