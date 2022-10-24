package ru.practicum.explore.model.compilation;

import lombok.Data;
import ru.practicum.explore.model.event.EventShortDto;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CompilationDto {
    private Long id;
    @Size(max = 200)
    private String title;
    private Boolean pinned; // закреплен или нет
    private List<EventShortDto> events; // список событий
}
