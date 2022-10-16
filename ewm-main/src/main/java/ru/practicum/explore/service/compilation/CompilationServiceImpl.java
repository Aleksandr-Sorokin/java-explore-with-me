package ru.practicum.explore.service.compilation;

import org.springframework.stereotype.Service;
import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.model.compilation.NewCompilationDto;
import ru.practicum.explore.storage.compilation.CompilationStorage;

import java.sql.SQLException;
import java.util.List;

@Service
public class CompilationServiceImpl implements CompilationService {
    private CompilationStorage compilationStorage;

    public CompilationServiceImpl(CompilationStorage compilationStorage) {
        this.compilationStorage = compilationStorage;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) throws SQLException {
        return compilationStorage.createCompilation(compilationDto);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationStorage.deleteCompilation(compId);
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        compilationStorage.deleteEventFromCompilation(compId, eventId);
    }

    @Override
    public void addEventForCompilation(Long compId, Long eventId) {
        compilationStorage.addEventForCompilation(compId, eventId);
    }

    @Override
    public void deletePinCompilation(Long compId) {
        compilationStorage.deletePinCompilation(compId);
    }

    @Override
    public void pinCompilation(Long compId) {
        compilationStorage.pinCompilation(compId);
    }

    @Override
    public List<CompilationDto> findCompilations(Boolean pinned, Integer from, Integer size) {
        return compilationStorage.findCompilations(pinned, from, size);
    }

    @Override
    public CompilationDto findCompilationsById(Long compId) {
        return compilationStorage.findCompilationsById(compId);
    }
}
