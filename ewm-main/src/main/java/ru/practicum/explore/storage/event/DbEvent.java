package ru.practicum.explore.storage.event;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.event.*;
import ru.practicum.explore.service.user.UserMapper;
import ru.practicum.explore.storage.category.CategoryStorage;
import ru.practicum.explore.storage.location.LocationStorage;
import ru.practicum.explore.storage.user.UserStorage;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explore.enums.State.*;

@Component
@RequiredArgsConstructor
public class DbEvent implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final CategoryStorage categoryStorage;
    private final UserStorage userStorage;
    private final LocationStorage locationStorage;
    private final ModelMapper mapper;
    private final UserMapper userMapper;

    @Override
    public List<Event> findEventByUserId(Long userId, Integer from, Integer size) {
        Collection<Event> collection = jdbcTemplate.query(
                "SELECT * FROM events WHERE user_id = ? LIMIT ? OFFSET ?;", this::makeEvent, userId, size, from);
        return List.copyOf(collection);
    }

    @Override
    public Event updateEventByUserId(Long userId, UpdateEventRequestDto eventDto) {
        try {
            int updateResult = jdbcTemplate.update("UPDATE events SET event_title = ?, event_annotation = ?," +
                            "event_description = ?,category_id = ?,event_date = ?, paid = ?, " +
                            "participation_limit = ?, state_name = 'PENDING' WHERE event_id = ? AND user_id = ? " +
                            "AND state_name NOT LIKE 'PUBLISHED';",
                    eventDto.getTitle(), eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getCategory(),
                    eventDto.getEventDate(), eventDto.getPaid(), eventDto.getParticipantLimit(),
                    eventDto.getEventId(), userId);
            if (updateResult < 1) {
                Event event = findEventById(eventDto.getEventId());
                if (!event.getInitiator().getId().equals(userId)) {
                    throw new SQLException("Данные не обновлены");
                }
                throw new RuntimeException("Публикация уже подтверждена");
            }
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Публикация уже подтверждена");
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Данные не обновлены объект не найден");
        }
        return findEventById(eventDto.getEventId());
    }

    @Override
    public Event createEvent(Long userId, NewEventDto eventDto, Long idLocation) {
        LocalDateTime createOn = LocalDateTime.now();
        Integer confirmed = 0;
        Event event = new Event();
        Boolean paid = false;
        Integer participationLimit = 0;
        Boolean moderation = true;
        if (eventDto.getPaid() != null) paid = eventDto.getPaid();
        if (eventDto.getParticipantLimit() != null) participationLimit = eventDto.getParticipantLimit();
        if (eventDto.getRequestModeration() != null) moderation = eventDto.getRequestModeration();
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO events (event_title, event_annotation, event_description, " +
                            "category_id, created, publish, event_date, user_id, location_id, paid, " +
                            "participation_limit, moderation, state_name, confirmed) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, eventDto.getTitle());
            statement.setString(2, eventDto.getAnnotation());
            statement.setString(3, eventDto.getDescription());
            statement.setLong(4, eventDto.getCategory());
            statement.setTimestamp(5, Timestamp.valueOf(createOn));
            statement.setTimestamp(6, null);
            statement.setTimestamp(7, Timestamp.valueOf(eventDto.getEventDate()));
            statement.setLong(8, userId);
            statement.setLong(9, idLocation);
            statement.setBoolean(10, paid);
            statement.setInt(11, participationLimit);
            statement.setBoolean(12, moderation);
            statement.setString(13, String.valueOf(PENDING));
            statement.setInt(14, confirmed);
            int answerRow = statement.executeUpdate();
            if (answerRow == 0) {
                throw new SQLException("Ошибка при добавлении события.");
            }
            try (ResultSet generatedKey = statement.getGeneratedKeys()) {
                if (generatedKey.next()) {
                    mapper.map(eventDto, event);
                    event.setId(generatedKey.getLong("event_id"));
                    event.setPaid(paid);
                    event.setRequestModeration(moderation);
                    event.setParticipantLimit(participationLimit);
                    event.setCategory(categoryStorage.findById(eventDto.getCategory()));
                    event.setCreatedOn(createOn);
                    event.setConfirmedRequests(confirmed);
                    event.setInitiator(userStorage.findUserById(userId));
                    event.setState(PENDING);
                } else {
                    throw new SQLException("Ошибка, нет сгенерированного id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return event;
    }

    @Override
    public Event findEventByEventIdAndUserId(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсутствует событие");
        }
        return event;
    }

    @Override
    public Event cancelEventBeforeModeration(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсутствует событие");
        }
        int result = jdbcTemplate.update("UPDATE events SET state_name = 'CANCELED' " +
                "WHERE state_name = 'PENDING' AND event_id = ? AND user_id = ?", eventId, userId);
        if (result < 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Статус не позволяет отменить событие");
        }
        return findEventById(eventId);
    }

    @Override
    public List<Event> searchEvent(List<Long> users, List<State> states, List<Long> categories,
                                   String rangeStart, String rangeEnd, Integer from, Integer size) {
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("SELECT * FROM events WHERE user_id IN (");
        users.stream().forEach(user -> sqlBuild.append("?,"));
        sqlBuild.append(")");
        sqlBuild.append(" AND state_name IN (");
        states.stream().forEach(state -> sqlBuild.append("?,"));
        sqlBuild.append(")");
        sqlBuild.append(" AND category_id IN (");
        categories.stream().forEach(category -> sqlBuild.append("?,"));
        sqlBuild.append(")");
        sqlBuild.append(" AND event_date BETWEEN ? AND ? LIMIT ? OFFSET ?;");
        String sql = sqlBuild.toString().replace(",)", ")");
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            for (int i = 0; i < users.size(); i++) {
                statement.setLong(index, users.get(i));
                index++;
            }
            for (int i = 0; i < states.size(); i++) {
                statement.setString(index, String.valueOf(states.get(i)));
                index++;
            }
            for (int i = 0; i < categories.size(); i++) {
                statement.setLong(index, categories.get(i));
                index++;
            }
            statement.setTimestamp(index, Timestamp.valueOf(rangeStart));
            index++;
            statement.setTimestamp(index, Timestamp.valueOf(rangeEnd));
            index++;
            statement.setInt(index, size);
            index++;
            statement.setInt(index, from);
            ResultSet resultSet = statement.executeQuery();
            List<Event> events = new ArrayList<>();
            while (resultSet.next()) {
                events.add(makeEvent(resultSet, resultSet.getRow()));
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Event editEvent(Long eventId, AdminUpdateEventRequest eventDto) {
        Long locationId = locationStorage.createLocation(eventDto.getLocation());
        int updateResult = jdbcTemplate.update("UPDATE events SET event_title = ?, event_annotation = ?," +
                        "event_description = ?,category_id = ?,event_date = ?, location_id = ?, paid = ?, " +
                        "participation_limit = ?, moderation = ? WHERE event_id = ?;",
                eventDto.getTitle(), eventDto.getAnnotation(), eventDto.getDescription(), eventDto.getCategory(),
                eventDto.getEventDate(), locationId, eventDto.getPaid(), eventDto.getParticipantLimit(),
                eventDto.getRequestModeration(), eventId);
        if (updateResult < 1) try {
            throw new SQLException("Данные не обновились");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return findEventById(eventId);
    }

    @Override
    public Event publishEvent(Long eventId) {
        try {
            int updateResult = jdbcTemplate.update("UPDATE events SET state_name = CASE WHEN event_date >= ? " +
                            "AND state_name = 'PENDING' THEN ? END WHERE event_id = ?;",
                    LocalDateTime.now().minusHours(1L), PUBLISHED.toString(), eventId);
            if (updateResult < 1) {
                throw new SQLException("Данные не обновлены");
            }
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Дата публикации менее чем через час или уже подтверждена/отменена");
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Данные не обновлены объект не найден");
        }
        return findEventById(eventId);
    }

    @Override
    public Event rejectEvent(Long eventId) {
        try {
            int updateResult = jdbcTemplate.update("UPDATE events SET state_name = CASE " +
                            "WHEN state_name = 'PENDING' THEN ? END WHERE event_id = ?;",
                    CANCELED.toString(), eventId);
            if (updateResult < 1) {
                throw new SQLException("Данные не обновлены");
            }
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Публикация уже подтверждена/отменена");
        } catch (SQLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Данные не обновлены объект не найден");
        }
        return findEventById(eventId);
    }

    @Override
    public Event findEventById(Long eventId) {
        Collection<Event> events = jdbcTemplate.query("SELECT * FROM events WHERE event_id = ?;",
                this::makeEvent, eventId);
        if (events == null || events.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Нет такого события");
        }
        return List.copyOf(events).get(0);
    }

    @Override
    public Event findEventByIdPublished(Long eventId) {
        Collection<Event> events = jdbcTemplate.query("SELECT * FROM events " +
                        "WHERE event_id = ? AND state_name = 'PUBLISHED';",
                this::makeEvent, eventId);
        if (events == null || events.size() == 0) return null;
        return List.copyOf(events).get(0);
    }

    @Override
    public List<EventShortDto> findFilterEvent(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               String sort, Integer from, Integer size) {
        StringBuilder sqlBuild = new StringBuilder();
        StringBuilder searchText = new StringBuilder();
        searchText.append("%" + text + "%");
        String searchQuery = searchText.toString();
        sqlBuild.append("SELECT * FROM events " +
                "WHERE state_name = 'PUBLISHED' " +
                "AND (LOWER(event_annotation) LIKE LOWER(?) OR LOWER(event_description) LIKE LOWER(?)) ");
        if (categories.size() >= 1 && categories.get(0) != 0) {
            sqlBuild.append("AND category_id IN (");
            categories.stream().forEach(category -> sqlBuild.append("?,"));
            sqlBuild.append(")");
        }
        sqlBuild.append(" AND paid = ? AND (event_date BETWEEN ? AND ? OR event_date >= '" +
                Timestamp.valueOf(LocalDateTime.now()) + "') ");
        if (onlyAvailable) {
            sqlBuild.append("AND (participation_limit = 0 OR participation_limit - confirmed > 0) LIMIT ? OFFSET ?;");
        } else {
            sqlBuild.append("LIMIT ? OFFSET ?;");
        }
        String sql = sqlBuild.toString().replace(",)", ")");
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            statement.setString(index, searchQuery);
            index++;
            statement.setString(index, searchQuery);
            index++;
            for (int i = 0; i < categories.size(); i++) {
                statement.setLong(index, categories.get(i));
                index++;
            }
            statement.setBoolean(index, paid);
            index++;
            statement.setTimestamp(index, Timestamp.valueOf(rangeStart));
            index++;
            statement.setTimestamp(index, Timestamp.valueOf(rangeEnd));
            index++;
            statement.setInt(index, size);
            index++;
            statement.setInt(index, from);
            ResultSet resultSet = statement.executeQuery();
            List<EventShortDto> events = new ArrayList<>();
            while (resultSet.next()) {
                events.add(makeEventShort(resultSet, resultSet.getRow()));
            }
            if (sort.toUpperCase().equals("EVENT_DATE")) {
                events.stream().sorted(((o1, o2) -> {
                    int result = o1.getEventDate().compareTo(o2.getEventDate());
                    return result * -1;
                })).collect(Collectors.toList());
            } else {
                events.stream().sorted(((o1, o2) -> {
                    int result = o1.getViews().compareTo(o2.getViews());
                    return result * -1;
                })).collect(Collectors.toList());
            }
            return events;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Event makeEvent(ResultSet rs, int rowNum) {
        try {
            Category category;
            try {
                category = categoryStorage.findById(rs.getLong("category_id"));
            } catch (ResponseStatusException e) {
                throw new RuntimeException();
            }

            Event event = new Event();
            event.setId(rs.getLong("event_id"));
            event.setTitle(rs.getString("event_title"));
            event.setAnnotation(rs.getString("event_annotation"));
            event.setDescription(rs.getString("event_description"));
            event.setCategory(category);
            event.setCreatedOn(rs.getTimestamp("created").toLocalDateTime());
            if (rs.getTimestamp("publish") != null) {
                event.setPublishedOn(rs.getTimestamp("publish").toLocalDateTime());
            }
            event.setEventDate(rs.getTimestamp("event_date").toLocalDateTime());
            event.setInitiator(userStorage.findUserById(rs.getLong("user_id")));
            event.setLocation(locationStorage.findLocation(rs.getLong("location_id")));
            event.setPaid(rs.getBoolean("paid"));
            event.setRequestModeration(rs.getBoolean("moderation"));
            event.setParticipantLimit(rs.getInt("participation_limit"));
            event.setState(getState(rs.getObject("state_name")));
            event.setConfirmedRequests(rs.getInt("confirmed"));
            //event.setViews(); добавляется в сервисе
            return event;
        } catch (ResponseStatusException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private EventShortDto makeEventShort(ResultSet rs, int rowNum) {
        try {
            EventShortDto event = new EventShortDto();
            event.setId(rs.getLong("event_id"));
            event.setTitle(rs.getString("event_title"));
            event.setAnnotation(rs.getString("event_annotation"));
            event.setCategory(categoryStorage.findById(rs.getLong("category_id")));
            event.setEventDate(rs.getTimestamp("event_date").toLocalDateTime());
            event.setInitiator(userMapper.toUserDto(userStorage.findUserById(rs.getLong("user_id"))));
            event.setPaid(rs.getBoolean("paid"));
            event.setConfirmedRequests(rs.getInt("confirmed"));
            //event.setViews(); добавляется в сервисе
            return event;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private State getState(Object state) {
        switch (state.toString()) {
            case "PENDING":
                return State.PENDING;
            case "PUBLISHED":
                return State.PUBLISHED;
            case "CANCELED":
                return State.CANCELED;
            default:
                throw new RuntimeException("Нет такого статуса " + state);
        }
    }

    public List<EventShortDto> findEventShortById(List<Long> eventId) {
        if (eventId.size() != 0) {
            StringBuilder sqlBuild = new StringBuilder();
            sqlBuild.append("SELECT * FROM events WHERE event_id IN (");
            eventId.stream().forEach(o -> sqlBuild.append("?,"));
            sqlBuild.append(")");
            String sql = sqlBuild.toString().replace(",)", ");");
            Collection<EventShortDto> events = jdbcTemplate.query(sql,
                    this::makeEventShort, eventId.stream().toArray(Long[]::new));
            return List.copyOf(events);
        } else {
            throw new RuntimeException("Пустой массив");
        }
    }

    @Override
    public List<Event> findEventByIdCategory(Long categoryId) {
        String sql = "SELECT * FROM events WHERE category_id = ?;";
        Collection<Event> collection = jdbcTemplate.query(sql, this::makeEvent, categoryId);
        return List.copyOf(collection);
    }
}
