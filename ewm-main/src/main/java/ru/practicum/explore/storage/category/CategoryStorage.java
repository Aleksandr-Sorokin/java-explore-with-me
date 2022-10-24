package ru.practicum.explore.storage.category;

import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.category.CategoryDto;
import ru.practicum.explore.model.category.NewCategoryDto;

import java.util.List;

public interface CategoryStorage {
    Category createCategory(NewCategoryDto categoryDto);

    void delete(Long id);

    Category findById(Long id);

    List<Category> findAll(Integer from, Integer size);

    Category updateCategory(CategoryDto categoryDto);
}
