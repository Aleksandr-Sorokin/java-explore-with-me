package ru.practicum.explore.storage.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.model.compilation.NewCompilationDto;
import ru.practicum.explore.model.event.EventShortDto;
import ru.practicum.explore.storage.event.DbEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DbCompilation implements CompilationStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DbEvent dbEvent;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("compilation")
                .usingColumns("compilation_name", "pinned")
                .usingGeneratedKeyColumns("compilation_id")
                .executeAndReturnKeyHolder(Map.of("compilation_name", compilationDto.getTitle(),
                        "pinned", compilationDto.getPinned()))
                .getKeys();
        CompilationDto compilations = new CompilationDto();
        compilations.setId((Long) keys.get("compilation_id"));
        compilations.setTitle(compilationDto.getTitle());
        compilations.setPinned(compilationDto.getPinned());
        compilations.setEvents(new ArrayList<>());
        return compilations;
    }

    @Override
    public void addEventForCompilation(Long compId, Long[] eventId) throws SQLException {
        String sql = "INSERT INTO compilation_event (compilation_id, event_id) VALUES (?, ?) " +
                "ON CONFLICT (compilation_id, event_id) DO NOTHING;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < eventId.length; i++) {
                statement.setLong(1, compId);
                statement.setLong(2, eventId[i]);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    @Override
    public void deleteCompilation(Long compId) {
        String sql = "DELETE FROM compilation WHERE compilation_id = ?;";
        jdbcTemplate.update(sql, compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        String sql = "DELETE FROM compilation_event WHERE compilation_id = ? AND event_id = ?;";
        jdbcTemplate.update(sql, compId, eventId);
    }

    @Override
    public void deletePinCompilation(Long compId) {
        String sql = "UPDATE compilation SET pinned = false WHERE compilation_id = ?;";
        jdbcTemplate.update(sql, compId);
    }

    @Override
    public void pinCompilation(Long compId) {
        String sql = "UPDATE compilation SET pinned = true WHERE compilation_id = ?;";
        jdbcTemplate.update(sql, compId);
    }

    @Override
    public List<CompilationDto> findCompilations(Integer from, Integer size) {
        String sql = "SELECT * FROM compilation LIMIT ? OFFSET ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, size, from);
        List<CompilationDto> compilations = new ArrayList<>();
        while (rowSet.next()) {
            CompilationDto compilationDto = new CompilationDto();
            Long compId = rowSet.getLong("compilation_id");
            String sqlEvent = "SELECT * FROM compilation_event WHERE compilation_id = ?;";
            Collection<Long> idEvents = jdbcTemplate.query(sqlEvent,
                    this::makeLong, compId);
            compilationDto.setId(compId);
            compilationDto.setTitle(rowSet.getString("compilation_name"));
            compilationDto.setPinned(rowSet.getBoolean("pinned"));
            List<EventShortDto> eventShorts = new ArrayList<>();
            List<Long> longList = List.copyOf(idEvents);
            if (longList.size() != 0) {
                eventShorts = dbEvent.findEventShortById(longList);
            }
            compilationDto.setEvents(eventShorts);
            compilations.add(compilationDto);
        }
        return compilations;
    }

    @Override
    public CompilationDto findCompilationsById(Long compId) {
        String sql = "SELECT * FROM compilation WHERE compilation_id = ?;";
        String sqlEvent = "SELECT * FROM compilation_event WHERE compilation_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, compId);
        Collection<Long> idEvents = jdbcTemplate.query(sqlEvent,
                this::makeLong, compId);
        CompilationDto compilationDto = new CompilationDto();
        if (rowSet.next()) {
            compilationDto.setId(compId);
            compilationDto.setTitle(rowSet.getString("compilation_name"));
            compilationDto.setPinned(rowSet.getBoolean("pinned"));
            List<EventShortDto> longList = new ArrayList<>();
            if (idEvents.size() != 0) {
                longList = dbEvent.findEventShortById(List.copyOf(idEvents));
            }
            compilationDto.setEvents(longList);
        }
        return compilationDto;
    }

    private Long makeLong(ResultSet rs, int rowNum) {
        try {
            Long l = rs.getLong("event_id");
            return l;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
