package ru.practicum.explore.model.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не должено быть пустым")
    private String name;
}
