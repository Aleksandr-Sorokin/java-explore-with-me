package ru.practicum.explore.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.service.category.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategory(@RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size) {

        return categoryService.findAll(from, size);
    }

    @GetMapping("/{catId}")
    public Category findById(@PathVariable Long catId) {
        return categoryService.findById(catId);
    }
}
