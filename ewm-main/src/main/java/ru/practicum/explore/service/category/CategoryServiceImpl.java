package ru.practicum.explore.service.category;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.category.CategoryDto;
import ru.practicum.explore.model.category.NewCategoryDto;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.storage.category.CategoryStorage;
import ru.practicum.explore.storage.event.EventStorage;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private CategoryStorage categoryStorage;
    private EventStorage eventStorage;
    private ModelMapper mapper;

    public CategoryServiceImpl(CategoryStorage categoryStorage, EventStorage eventStorage, ModelMapper mapper) {
        this.categoryStorage = categoryStorage;
        this.eventStorage = eventStorage;
        this.mapper = mapper;
    }

    @Override
    public Category createCategory(NewCategoryDto categoryDto) {
        return categoryStorage.createCategory(categoryDto);
    }

    @Override
    public void delete(Long id) {
        List<Event> events = eventStorage.findEventByIdCategory(id);
        if (events.size() == 0) {
            categoryStorage.delete(id);
        } else throw new ResponseStatusException(HttpStatus.CONFLICT, "Категория используется в других событиях");
    }

    @Override
    public Category findById(Long id) {
        return categoryStorage.findById(id);
    }

    @Override
    public List<Category> findAll(Integer from, Integer size) {
        return categoryStorage.findAll(from, size);
    }

    @Override
    public Category updateCategory(CategoryDto categoryDto) {
        categoryStorage.findById(categoryDto.getId());
        Category category = categoryStorage.updateCategory(categoryDto);
        return category;
    }
}
