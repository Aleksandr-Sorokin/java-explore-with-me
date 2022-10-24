package ru.practicum.explore.service.comment;

import ru.practicum.explore.model.comment.Comment;
import ru.practicum.explore.model.comment.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Comment comment);

    CommentDto findCommentById(Long commentId);

    List<CommentDto> findCommentsByEventsId(Long eventsId);
}
