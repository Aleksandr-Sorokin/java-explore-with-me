package ru.practicum.explore.model.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    private ModelMapper mapper;

    public CategoryMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public Category toEntity(CategoryDto categoryDto) {
        if (categoryDto != null) {
            Category category = new Category();
            mapper.map(categoryDto, category);
            category.setName(categoryDto.getName());
            return category;
        } else {
            return null;
        }
    }
}
