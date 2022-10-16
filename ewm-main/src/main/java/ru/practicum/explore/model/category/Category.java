package ru.practicum.explore.model.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Category {
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    // часть тестов проходят только с названием переменной category_name
    private String name;
}
