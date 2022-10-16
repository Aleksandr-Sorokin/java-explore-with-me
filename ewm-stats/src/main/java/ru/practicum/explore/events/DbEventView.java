package ru.practicum.explore.events;


import io.micrometer.core.lang.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component
public class DbEventView implements EventViewStorage {
    private JdbcTemplate jdbcTemplate;

    public DbEventView(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EventView> addEventView(List<Long> eventId, @Nullable String httpAddress, String ipAddress) {
        LocalDateTime dateView = LocalDateTime.now();
        String sqlForUrl = "INSERT INTO view_events_url (event_id, date_view, http_address, ip_address) " +
                "VALUES (?, ?, ?, ?);";
        String sqlForView = "INSERT INTO view_events (event_id, views) VALUES (?, ?) ON CONFLICT (event_id) DO " +
                "UPDATE SET views = excluded.views + 1;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sqlForView);
            for (int i = 0; i < eventId.size(); i++) {
                statement.setLong(1, eventId.get(i));
                statement.setInt(2, 1);
                statement.addBatch();
            }
            int[] resultBatch = statement.executeBatch();
            statement.close();
            statement = connection.prepareStatement(sqlForUrl, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < eventId.size(); i++) {
                statement.setLong(1, eventId.get(i));
                statement.setTimestamp(2, Timestamp.valueOf(dateView));
                statement.setString(3, httpAddress);
                statement.setString(4, ipAddress);
                statement.addBatch();
            }
            int[] resultBatchUrl = statement.executeBatch();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM view_events WHERE event_id IN (");
        eventId.stream().forEach(aLong -> sqlBuilder.append("?,"));
        sqlBuilder.append(");");
        String sql = sqlBuilder.toString().replace(",)", ")");
        Collection<EventView> collection = jdbcTemplate.query(sql, this::makeEventView, eventId.toArray(Long[]::new));
        return List.copyOf(collection);
    }

    private EventView makeEventView(ResultSet rs, int rowNum) throws SQLException {
        EventView event = new EventView();
        event.setEventId(rs.getLong("event_id"));
        event.setView(rs.getInt("views"));
        return event;
    }

    @Override
    public List<Integer> getEventViewById(Long eventId, @Nullable String httpAddress, String ipAddress) {
        Collection<Integer> collection = jdbcTemplate.query("SELECT views FROM view_events WHERE event_id = ?",
                ((rs, rowNum) -> rs.getInt("views")), eventId);
        return List.copyOf(collection);
    }
}
