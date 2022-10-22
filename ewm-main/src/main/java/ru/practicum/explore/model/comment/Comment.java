package ru.practicum.explore.model.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Comment {
    Long id;
    @NotBlank
    String text;
    @NotNull
    Long eventId;
    @NotNull
    Long userId;
}
