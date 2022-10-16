package ru.practicum.explore.controller.admins;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.model.compilation.NewCompilationDto;
import ru.practicum.explore.service.compilation.CompilationService;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompillationAdminController {
    public final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(@RequestBody NewCompilationDto compilationDto) throws SQLException {
        return compilationService.createCompilation(compilationDto);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventFromCompilation(@PathVariable Long compId,
                                           @PathVariable Long eventId) {
        compilationService.deleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void addEventForCompilation(@PathVariable Long compId,
                                       @PathVariable Long eventId) {
        compilationService.addEventForCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void deletePinCompilation(Long compId) {
        compilationService.deletePinCompilation(compId);
    }

    @PatchMapping("/{compId}/pin")
    public void pinCompilation(Long compId) {
        compilationService.pinCompilation(compId);
    }
}
