package ru.practicum.explore.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.service.compilation.CompilationService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationsPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> findCompilations(@RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.findCompilations(from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto findCompilationsById(@PathVariable @Positive Long compId) {
        return compilationService.findCompilationsById(compId);
    }
}
