package ru.practicum.explore.controller.admins;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.category.CategoryDto;
import ru.practicum.explore.model.category.NewCategoryDto;
import ru.practicum.explore.service.category.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    public Category create(@Valid @RequestBody NewCategoryDto categoryDto) {
        if (categoryDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Категория не может быть пустой");
        }
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping
    public Category update(@Valid @RequestBody CategoryDto categoryDto) {
        if (categoryDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Категория не может быть пустой");
        }
        return categoryService.updateCategory(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable @Positive Long categoryId) {
        categoryService.delete(categoryId);
    }
}
