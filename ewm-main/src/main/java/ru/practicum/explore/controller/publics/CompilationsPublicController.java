package ru.practicum.explore.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.compilation.CompilationDto;
import ru.practicum.explore.service.compilation.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationsPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> findCompilations(@RequestParam Boolean pinned,
                                                 @RequestParam Integer from,
                                                 @RequestParam Integer size) {
        return compilationService.findCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto findCompilationsById(@PathVariable Long compId) {
        return compilationService.findCompilationsById(compId);
    }
}
