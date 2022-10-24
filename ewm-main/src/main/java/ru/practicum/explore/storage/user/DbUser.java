package ru.practicum.explore.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.user.NewUserDto;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.service.user.UserMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DbUser implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    @Override
    public User createUser(NewUserDto newUser) {
        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingColumns("user_name", "user_email")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKeyHolder(Map.of(
                        "user_name", newUser.getName(),
                        "user_email", newUser.getEmail()))
                .getKeys();
        User user = userMapper.toEntity(newUser);
        user.setId((Long) keys.get("user_id"));
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE user_id = ?;";
        jdbcTemplate.update(sql, ps -> {
            ps.setLong(1, id);
        });
    }

    @Override
    public User findUserById(Long id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?;", id);
        if (rowSet.next()) {
            User user = new User();
            user.setId(rowSet.getLong("user_id"));
            user.setName(rowSet.getString("user_name"));
            user.setEmail(rowSet.getString("user_email"));
            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь не найден"));
        }
    }

    @Override
    public List<User> findUsers(List<Long> ids, Integer from, Integer size) {
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("SELECT * FROM users WHERE user_id IN (");
        ids.stream().forEach(user -> sqlBuild.append("?,"));
        sqlBuild.append(") LIMIT ? OFFSET ?;");
        String sql = sqlBuild.toString().replace(",)", ")");
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            int index = 1;
            for (int i = 0; i < ids.size(); i++) {
                statement.setLong(index, ids.get(i));
                index++;
            }
            statement.setInt(index, size);
            index++;
            statement.setInt(index, from);
            ResultSet resultSet = statement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(makeUser(resultSet, resultSet.getRow()));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User makeUser(ResultSet rs, int rowNum) {
        try {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setName(rs.getString("user_name"));
            user.setEmail(rs.getString("user_email"));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
