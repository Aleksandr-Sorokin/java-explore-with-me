package ru.practicum.explore.service.compilation;

import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.model.compilation.NewCompilationDto;

import java.sql.SQLException;
import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto compilationDto) throws SQLException;

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventForCompilation(Long compId, Long eventId);

    void deletePinCompilation(Long compId);

    void pinCompilation(Long compId);

    List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findCompilationsById(Long compId);
}
