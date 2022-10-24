package ru.practicum.explore.model.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не должено быть пустым")
    @Size(max = 200)
    private String name;
}
