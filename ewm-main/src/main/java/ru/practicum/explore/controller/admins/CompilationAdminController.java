package ru.practicum.explore.controller.admins;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.model.compilation.NewCompilationDto;
import ru.practicum.explore.service.compilation.CompilationService;

import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody NewCompilationDto compilationDto) {
        if (compilationDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Подборка не может быть пустой");
        }
        return compilationService.createCompilation(compilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable @Positive Long compId,
                                           @PathVariable @Positive Long eventId) {
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventForCompilation(@PathVariable @Positive Long compId,
                                       @PathVariable @Positive Long eventId) {
        compilationService.addEventForCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void deletePinCompilation(@PathVariable @Positive Long compId) {
        compilationService.deletePinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(@PathVariable @Positive Long compId) {
        compilationService.pinCompilation(compId);
    }
}
