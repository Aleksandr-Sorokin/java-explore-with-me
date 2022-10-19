package ru.practicum.explore.service.compilation;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.model.compilation.NewCompilationDto;
import ru.practicum.explore.storage.compilation.CompilationStorage;

import java.sql.SQLException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private CompilationStorage compilationStorage;
    private ModelMapper mapper;

    public CompilationServiceImpl(CompilationStorage compilationStorage, ModelMapper mapper) {
        this.compilationStorage = compilationStorage;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        CompilationDto compilation;
        try {
            compilation = compilationStorage.createCompilation(compilationDto);
            if (compilationDto.getEvents().size() != 0) {
                Long[] compilationsId = compilationDto.getEvents().stream().toArray(Long[]::new);
                compilationStorage.addEventForCompilation(compilation.getId(), compilationsId);
                mapper.map(compilationStorage.findCompilationsById(compilation.getId()), compilation);
                return compilation;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return compilation;
    }


    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationStorage.deleteCompilation(compId);
    }

    @Override
    @Transactional
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        compilationStorage.deleteEventFromCompilation(compId, eventId);
    }

    @Override
    @Transactional
    public void addEventForCompilation(Long compId, Long eventId) {
        Long[] events = {eventId};
        try {
            compilationStorage.addEventForCompilation(compId, events);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deletePinCompilation(Long compId) {
        compilationStorage.deletePinCompilation(compId);
    }

    @Override
    @Transactional
    public void pinCompilation(Long compId) {
        compilationStorage.pinCompilation(compId);
    }

    @Override
    public List<CompilationDto> findCompilations(Integer from, Integer size) {
        return compilationStorage.findCompilations(from, size);
    }

    @Override
    public CompilationDto findCompilationsById(Long compId) {
        return compilationStorage.findCompilationsById(compId);
    }
}
