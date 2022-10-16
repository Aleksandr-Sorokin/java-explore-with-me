package ru.practicum.explore.model.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class NewCategoryDto {
    @NotBlank(message = "Название не может быть пустым")
    private String name;
}
