package ru.practicum.explore.service.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.category.CategoryDto;
import ru.practicum.explore.model.category.NewCategoryDto;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.storage.category.CategoryStorage;
import ru.practicum.explore.storage.event.EventStorage;

import java.util.List;

@Service
@Transactional(readOnly = true)
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
    @Transactional
    public Category createCategory(NewCategoryDto categoryDto) {
        return categoryStorage.createCategory(categoryDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        List<Event> events = eventStorage.findEventByIdCategory(id);
        if (events.size() == 0) {
            categoryStorage.delete(id);
        }
    }

    @Override
    public Category findById(Long id) {
        try {
            Category category = categoryStorage.findById(id);
            return category;
        } catch (ResponseStatusException e) {
            e.getMessage();
        }
        return null;
    }

    @Override
    public List<Category> findAll(Integer from, Integer size) {
        return categoryStorage.findAll(from, size);
    }

    @Override
    @Transactional
    public Category updateCategory(CategoryDto categoryDto) {
        categoryStorage.findById(categoryDto.getId());
        Category category = categoryStorage.updateCategory(categoryDto);
        return category;
    }
}
