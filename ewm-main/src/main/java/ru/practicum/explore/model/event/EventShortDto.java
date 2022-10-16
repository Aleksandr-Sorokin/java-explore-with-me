package ru.practicum.explore.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.user.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class EventShortDto {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String annotation; // Краткое описание
    @NotNull
    private Category category;
    private Integer confirmedRequests; // Количество одобренных заявок на участие в данном событии
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private UserDto initiator;
    private Boolean paid; // Нужно ли оплачивать участие
    private Integer views; // Количество просмотрев события
}
