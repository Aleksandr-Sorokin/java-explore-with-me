package ru.practicum.explore.model.compilation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class NewCompilationDto {
    @NotBlank
    private String title;
    private List<Long> events; // Список идентификаторов событий входящих в подборку
    private Boolean pinned; // Закреплена ли подборка на главной странице сайта
}
