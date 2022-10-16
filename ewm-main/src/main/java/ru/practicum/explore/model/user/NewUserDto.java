package ru.practicum.explore.model.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class NewUserDto {
    @NotBlank(message = "Имя не должено быть пустым")
    private String name;
    @Email(message = "Проверьте корректность email")
    private String email;
}
