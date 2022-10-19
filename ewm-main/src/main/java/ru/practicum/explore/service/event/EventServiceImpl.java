package ru.practicum.explore.service.event;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.event.*;
import ru.practicum.explore.storage.event.EventStorage;
import ru.practicum.explore.storage.event.participation.ParticipationStorage;
import ru.practicum.explore.storage.location.LocationStorage;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private EventStorage eventStorage;
    private LocationStorage locationStorage;
    private ParticipationStorage participationStorage;
    private static final String API_PREFIX = "/views";
    private WebClient webClient;

    public EventServiceImpl(EventStorage eventStorage, LocationStorage locationStorage,
                            ParticipationStorage participationStorage, WebClient webClient) {
        this.eventStorage = eventStorage;
        this.locationStorage = locationStorage;
        this.participationStorage = participationStorage;
        this.webClient = webClient;
    }

    //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
    //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
    //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события,
    // которые произойдут позже текущей даты и времени
    //информация о каждом событии должна включать в себя количество просмотров и количество уже
    // одобренных заявок на участие
    //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить
    // в сервисе статистики
    @Override
    public List<EventShortDto> findFilterEvent(String text, List<Long> categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sort, Integer from, Integer size, HttpServletRequest request) {
        List<EventShortDto> events = eventStorage.findFilterEvent(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
        List<Long> eventId = new ArrayList<>();
        events.stream().forEach(event -> eventId.add(event.getId()));
        Map<Long, Integer> eventsView = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(API_PREFIX)
                        .queryParam("eventId", eventId)
                        .queryParam("httpAddress", request.getRequestURI())
                        .queryParam("ipAddress", request.getRemoteAddr())
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        events.stream().filter(eventShortDto -> !eventShortDto.equals(null))
                .forEach(event -> event.setViews(Integer.valueOf(eventsView.get(event.getId().toString()))));
        return events;
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventStorage.findEventById(eventId);
    }

    //событие должно быть опубликовано
    //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
    //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    @Override
    public Event findEventByIdPublished(Long eventId, HttpServletRequest request) {
        Event event = eventStorage.findEventByIdPublished(eventId);
        Integer eventView = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(API_PREFIX + "/{eventId}")
                        //.query(String.valueOf(eventId))
                        .queryParam("httpAddress", request.getRequestURI())
                        .queryParam("ipAddress", request.getRemoteAddr())
                        .build(eventId))
                .retrieve()
                .bodyToMono(Integer.class)
                .block();
        event.setViews(eventView);
        return event;
    }

    @Override
    public List<Event> findEventByUserId(Long userId, Integer from, Integer size) {
        return eventStorage.findEventByUserId(userId, from, size);
    }

    @Override
    @Transactional
    public Event updateEventByUserId(Long userId, UpdateEventRequestDto eventDto) {
        if (eventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            return eventStorage.updateEventByUserId(userId, eventDto);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Дата и время на которые намечено событие не может быть раньше, " +
                            "чем через два часа от текущего момента");
        }
    }

    @Override
    @Transactional
    public Event createEvent(Long userId, NewEventDto eventDto) {
        if (eventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            try {
                Long idLocation = locationStorage.createLocation(eventDto.getLocation());
                return eventStorage.createEvent(userId, eventDto, idLocation);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Дата и время на которые намечено событие не может быть раньше, " +
                            "чем через два часа от текущего момента");
        }
    }

    @Override
    public Event findEventByEventIdAndUserId(Long userId, Long eventId) {
        return eventStorage.findEventByEventIdAndUserId(userId, eventId);
    }

    @Override
    @Transactional
    public Event cancelEventBeforeModeration(Long userId, Long eventId) {
        return eventStorage.cancelEventBeforeModeration(userId, eventId);
    }

    @Override
    public List<Event> searchEvent(List<Long> users, List<State> states, List<Long> categories,
                                   String rangeStart, String rangeEnd, Integer from, Integer size) {
        return eventStorage.searchEvent(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @Override
    @Transactional
    public Event editEvent(Long eventId, AdminUpdateEventRequest eventDto) {
        Event event = eventStorage.findEventById(eventId);
        if (eventDto.getPaid() == null) eventDto.setPaid(event.getPaid());
        if (eventDto.getTitle() == null) eventDto.setTitle(event.getTitle());
        if (eventDto.getAnnotation() == null) eventDto.setAnnotation(event.getAnnotation());
        if (eventDto.getDescription() == null) eventDto.setDescription(event.getDescription());
        if (eventDto.getCategory() == null) eventDto.setCategory(event.getCategory().getId());
        if (eventDto.getEventDate() == null) eventDto.setEventDate(event.getEventDate());
        if (eventDto.getParticipantLimit() == null) eventDto.setParticipantLimit(event.getParticipantLimit());
        if (eventDto.getRequestModeration() == null) eventDto.setRequestModeration(event.getRequestModeration());
        if (eventDto.getLocation() == null) eventDto.setLocation(event.getLocation());
        try {
            return eventStorage.editEvent(eventId, eventDto);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Event publishEvent(Long eventId) {
        try {
            return eventStorage.publishEvent(eventId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Event rejectEvent(Long eventId) {
        return eventStorage.rejectEvent(eventId);
    }
}