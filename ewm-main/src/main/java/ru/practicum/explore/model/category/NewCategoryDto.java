package ru.practicum.explore.model.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 200)
    private String name;
}
