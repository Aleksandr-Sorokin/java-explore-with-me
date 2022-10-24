package ru.practicum.explore.model.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class User {
    private Long id;
    @NotBlank(message = "Имя не должено быть пустым")
    @Size(max = 200)
    private String name;
    @Email(message = "Проверьте корректность email")
    @Size(max = 200)
    private String email;
}
