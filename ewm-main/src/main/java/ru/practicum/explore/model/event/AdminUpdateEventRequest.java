package ru.practicum.explore.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore.model.location.Location;

import java.time.LocalDateTime;

@Data
public class AdminUpdateEventRequest {
    private String title;
    private String annotation; // Краткое описание
    private String description;  // Полное описание события
    private Long category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private Location location;
    private Boolean paid; // Нужно ли оплачивать участие
    private Integer participantLimit; // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие
}
