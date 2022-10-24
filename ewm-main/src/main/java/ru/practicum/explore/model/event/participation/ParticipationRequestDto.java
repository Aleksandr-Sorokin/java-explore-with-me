package ru.practicum.explore.model.event.participation;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explore.enums.Status;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private Long event;
    private Long requester; // Идентификатор пользователя, отправившего заявку
    private Status status;
}
