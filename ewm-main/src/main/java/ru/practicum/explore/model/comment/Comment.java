package ru.practicum.explore.model.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Long id;
    @NotBlank() @Size(max = 2000)
    private String text;
    @NotNull
    private Long eventId;
    @NotNull
    private Long userId;
}
