package ru.practicum.explore.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore.enums.State;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.location.Location;
import ru.practicum.explore.model.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class Event {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String annotation; // Краткое описание
    @NotBlank
    private String description;  // Полное описание события
    @NotNull
    private Category category;
    private Integer confirmedRequests; // Количество одобренных заявок на участие в данном событии
    private LocalDateTime createdOn;  // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private User initiator;
    @NotNull
    private Location location;
    private Boolean paid; // Нужно ли оплачивать участие
    private Integer participantLimit; // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие
    private State state;
    private Integer views; // Количество просмотрев события
}
