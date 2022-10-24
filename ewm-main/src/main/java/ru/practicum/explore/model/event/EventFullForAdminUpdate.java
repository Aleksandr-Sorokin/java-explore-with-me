package ru.practicum.explore.model.event;

import lombok.Data;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.location.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class EventFullForAdminUpdate {
    private Long id;
    @Size(max = 200)
    private String title;
    @Size(max = 1000)
    private String annotation; // Краткое описание
    @Size(max = 2000)
    private String description;  // Полное описание события
    private Long category;
    private Integer confirmedRequests; // Количество одобренных заявок на участие в данном событии
    private LocalDateTime createdOn;  // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private Long initiator;
    private Location location;
    private Boolean paid; // Нужно ли оплачивать участие
    private Integer participantLimit; // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие
    private State state;
}
