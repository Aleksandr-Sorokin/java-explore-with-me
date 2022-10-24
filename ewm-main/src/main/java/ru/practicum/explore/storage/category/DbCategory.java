package ru.practicum.explore.storage.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.category.CategoryDto;
import ru.practicum.explore.model.category.CategoryMapper;
import ru.practicum.explore.model.category.NewCategoryDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DbCategory implements CategoryStorage {
    private final JdbcTemplate jdbcTemplate;
    private final CategoryMapper mapper;

    @Override
    public Category createCategory(NewCategoryDto categoryDto) {
        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("category")
                .usingColumns("category_name")
                .usingGeneratedKeyColumns("category_id")
                .executeAndReturnKeyHolder(Map.of("category_name", categoryDto.getName()))
                .getKeys();
        Category category = findById((Long) keys.get("category_id"));
        return category;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM category WHERE category_id = ?;";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Category findById(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM category WHERE category_id = ?", id);
        Category category = new Category();
        if (rowSet.next()) {
            category.setId(rowSet.getLong("category_id"));
            category.setName(rowSet.getString("category_name"));
            return category;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event with id=" + id + " was not found.");
        }
    }

    @Override
    public List<Category> findAll(Integer from, Integer size) {
        String sql = "SELECT * FROM category LIMIT ? OFFSET ?;";
        Collection<Category> categories = jdbcTemplate.query(sql, this::makeCategory, size, from);
        return List.copyOf(categories);
    }

    @Override
    public Category updateCategory(CategoryDto categoryDto) {
        String sql = "UPDATE category SET category_name = ? WHERE category_id = ?;";
        int insert = jdbcTemplate.update(sql, categoryDto.getName(), categoryDto.getId());
        if (insert < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Данные не обновились");
        }
        return mapper.toEntity(categoryDto);
    }

    private Category makeCategory(ResultSet rs, int rowNum) {
        try {
            Category category = new Category();
            category.setId(rs.getLong("category_id"));
            category.setName(rs.getString("category_name"));
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
