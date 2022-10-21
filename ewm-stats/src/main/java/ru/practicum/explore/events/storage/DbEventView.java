package ru.practicum.explore.events.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.explore.events.model.EndpointHit;
import ru.practicum.explore.events.model.ViewStats;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DbEventView implements EventViewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEventView(EndpointHit endpointHit) {
        LocalDateTime dateView = LocalDateTime.now();
        if (endpointHit.getTimestamp() != null) {
            dateView = endpointHit.getTimestamp();
        }
        String sqlForUrl = "INSERT INTO view_events_url (app, date_view, uri_address, ip_address) " +
                "VALUES (?, ?, ?, ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sqlForUrl, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, endpointHit.getApp());
            statement.setTimestamp(2, Timestamp.valueOf(dateView));
            statement.setString(3, endpointHit.getUri());
            statement.setString(4, endpointHit.getIp());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ViewStats> getAppView(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("SELECT * FROM view_events_url WHERE date_view BETWEEN ? AND ?");
        if (uris.size() > 0) {
            sqlBuild.append(" AND uri_address IN (");
            for (int i = 0; i < uris.size(); i++) {
                sqlBuild.append("?,");
            }
            sqlBuild.append(")");
        }
        String sql = sqlBuild.toString().replace(",)", ")");
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            statement.setTimestamp(index, Timestamp.valueOf(start));
            index++;
            statement.setTimestamp(index, Timestamp.valueOf(end));
            index++;
            for (int i = 0; i < uris.size(); i++) {
                statement.setString(index, uris.get(i));
                index++;
            }
            ResultSet resultSet = statement.executeQuery();
            List<ViewStats> stats = new ArrayList<>();
            Map<ViewStats, Integer> statsSum = new HashMap<>();
            while (resultSet.next()) {
                ViewStats viewStats = new ViewStats();
                viewStats.setApp(resultSet.getString("app"));
                viewStats.setUri(resultSet.getString("uri_address"));
                if (!statsSum.containsKey(viewStats)) {
                    statsSum.put(viewStats, 1);
                } else {
                    statsSum.put(viewStats, viewStats.getHits() + 1);
                }
            }
            for (Map.Entry<ViewStats, Integer> entry : statsSum.entrySet()) {
                ViewStats viewStats = entry.getKey();
                viewStats.setHits(entry.getValue());
                stats.add(viewStats);
            }
            return stats;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
