package ru.practicum.explore.storage.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.practicum.explore.model.comment.Comment;
import ru.practicum.explore.model.comment.CommentDto;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.storage.event.EventStorage;
import ru.practicum.explore.storage.user.UserStorage;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DbComment implements CommentStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Override
    public CommentDto addComment(Comment comment) throws SQLException {
        LocalDateTime created = LocalDateTime.now();
        int result = -1;
        PreparedStatement statement;
        CommentDto commentDto = new CommentDto();
        String sql = "INSERT INTO comment_event (text_comment, event_id, user_id, created) VALUES (?, ?, ?, ?);";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, comment.getText());
            statement.setLong(2, comment.getEventId());
            statement.setLong(3, comment.getUserId());
            statement.setTimestamp(4, Timestamp.valueOf(created));
            result = statement.executeUpdate();
            if (result == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    commentDto = makeCommentDto(resultSet, resultSet.getRow());
                }
            }
            return commentDto;
        }
    }

    @Override
    public CommentDto findCommentById(Long commentId) throws SQLException {
        CommentDto commentDto = null;
        PreparedStatement statement;
        String sql = "SELECT * FROM comment_event WHERE comment_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, commentId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                commentDto = makeCommentDto(resultSet, resultSet.getRow());
            }
        }
        return commentDto;
    }

    @Override
    public List<CommentDto> findCommentsByEventsId(Long eventsId) throws SQLException {
        CommentDto commentDto;
        List<CommentDto> comments = new ArrayList<>();
        PreparedStatement statement;
        String sql = "SELECT * FROM comment_event WHERE event_id = ?;";
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, eventsId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                commentDto = makeCommentDto(resultSet, resultSet.getRow());
                comments.add(commentDto);
            }
        }
        return comments;
    }

    private CommentDto makeCommentDto(ResultSet rs, int rowNum) {
        CommentDto comment = new CommentDto();
        try {
            Event event = eventStorage.findEventById(rs.getLong("event_id"));
            User user = userStorage.findUserById(rs.getLong("user_id"));
            comment.setText(rs.getString("text_comment"));
            comment.setEventTitle(event.getTitle());
            comment.setUserName(user.getName());
            return comment;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
