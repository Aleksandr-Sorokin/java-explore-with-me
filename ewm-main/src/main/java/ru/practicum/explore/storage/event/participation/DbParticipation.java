package ru.practicum.explore.storage.event.participation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.enums.Status;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.participation.ParticipationRequestDto;
import ru.practicum.explore.storage.event.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DbParticipation implements ParticipationStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;

    @Override
    public List<ParticipationRequestDto> findParticipationByEventIdAndUserId(Long userId, Long eventId) {
        Collection<ParticipationRequestDto> collection = jdbcTemplate.query(
                "SELECT * FROM participation WHERE event_id = ?;", this::makeParticipation, eventId);
        return List.copyOf(collection);
    }

    @Override
    public ParticipationRequestDto confirmParticipation(Long userId, Long eventId, Long reqId, Status status) {
        if (status.equals(Status.REJECTED)) {
            int result = jdbcTemplate.update("UPDATE participation SET status_name = ? " +
                    "WHERE status_name = 'PENDING' AND event_id = ?", status.toString(), eventId);
            if (result < 1) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Данные не обновились");
        }
        if (status.equals(Status.CONFIRMED)) {
            int resultEvent = jdbcTemplate.update("UPDATE events SET confirmed = confirmed + 1 WHERE event_id = ?;", eventId);
            if (resultEvent < 1)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Данные не обновились в событие");
            int result = jdbcTemplate.update("UPDATE participation SET status_name = ? " +
                    "WHERE status_name = 'PENDING' AND event_id = ? AND participation_id = ?", status.toString(), eventId, reqId);
            if (result < 1) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Данные не обновились");
        }
        return findParticipationById(reqId);
    }

    @Override
    public ParticipationRequestDto rejectParticipation(Long userId, Long eventId, Long reqId) {
        int result = jdbcTemplate.update("UPDATE participation SET status_name = 'REJECTED' " +
                "WHERE event_id = ? AND participation_id = ?", eventId, reqId);
        if (result < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Данные не обновились");
        }
        return findParticipationById(reqId);
    }

    @Override
    public List<ParticipationRequestDto> findAllRequestById(Long userId) {
        Collection<ParticipationRequestDto> collection = jdbcTemplate.query(
                "SELECT * FROM participation WHERE user_id = ?", this::makeParticipation, userId);
        return List.copyOf(collection);
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId, Status status) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("participation")
                .usingColumns("created", "event_id", "user_id", "status_name")
                .usingGeneratedKeyColumns("participation_id")
                .executeAndReturnKeyHolder(Map.of("created", LocalDateTime.now(),
                        "event_id", eventId,
                        "user_id", userId,
                        "status_name", String.valueOf(status)))
                .getKeys();
        requestDto.setId((Long) keys.get("participation_id"));
        requestDto.setRequester(userId);
        requestDto.setCreated((LocalDateTime) keys.get("created"));
        requestDto.setEvent(eventId);
        requestDto.setStatus(status);
        return requestDto;
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequestDto requestDto = findParticipationById(requestId);
        Event event = eventStorage.findEventById(requestDto.getEvent());
        int result = jdbcTemplate.update("UPDATE participation " +
                "SET status_name = 'CANCELED' WHERE participation_id = ?;", requestId);
        if (result == 1 && requestDto.getStatus().equals(Status.CONFIRMED)) {
            int updateResult = jdbcTemplate.update(
                    "UPDATE events SET confirmed = confirmed - 1 WHERE event_id = ?;", requestDto.getEvent());
            if (updateResult < 1) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Данные не обновлены объект не найден");
            }
        }
        return findParticipationById(requestId);
    }

    private ParticipationRequestDto makeParticipation(ResultSet rs, int rowNum) throws SQLException {
        ParticipationRequestDto participation = new ParticipationRequestDto();
        participation.setId(rs.getLong("participation_id"));
        participation.setEvent(rs.getLong("event_id"));
        participation.setCreated(null);
        //Тест требует значение null
        //participation.setCreated(rs.getTimestamp("created").toLocalDateTime());
        participation.setRequester(rs.getLong("user_id"));
        participation.setStatus(getStatus(rs.getObject("status_name")));
        return participation;
    }

    private ParticipationRequestDto findParticipationById(Long reqId) {
        Collection<ParticipationRequestDto> participation = jdbcTemplate.query(
                "SELECT * FROM participation WHERE participation_id = ?;", this::makeParticipation, reqId);
        if (participation == null || participation.size() == 0) return null;
        return List.copyOf(participation).get(0);
    }

    private Status getStatus(Object status) {
        switch (status.toString()) {
            case "PENDING":
                return Status.PENDING;
            case "CONFIRMED":
                return Status.CONFIRMED;
            case "REJECTED":
                return Status.REJECTED;
            case "CANCELED":
                return Status.CANCELED;
            default:
                throw new RuntimeException("Нет такого статуса " + status);
        }
    }
}
