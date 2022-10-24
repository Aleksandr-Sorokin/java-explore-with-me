package ru.practicum.explore.storage.comment;

import ru.practicum.explore.model.comment.Comment;
import ru.practicum.explore.model.comment.CommentDto;

import java.sql.SQLException;
import java.util.List;

public interface CommentStorage {
    CommentDto addComment(Comment comment) throws SQLException;

    CommentDto findCommentById(Long commentId) throws SQLException;

    List<CommentDto> findCommentsByEventsId(Long eventsId) throws SQLException;
}
