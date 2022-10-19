package ru.practicum.explore.storage.compilation;

import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.model.compilation.NewCompilationDto;

import java.sql.SQLException;
import java.util.List;

public interface CompilationStorage {
    CompilationDto createCompilation(NewCompilationDto compilationDto) throws SQLException;

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventForCompilation(Long compId, Long[] eventId) throws SQLException;

    void deletePinCompilation(Long compId);

    void pinCompilation(Long compId);

    List<CompilationDto> findCompilations(Integer from, Integer size);

    CompilationDto findCompilationsById(Long compId);
}
