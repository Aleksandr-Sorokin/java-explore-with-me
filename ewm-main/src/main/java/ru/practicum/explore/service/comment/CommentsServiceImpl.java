package ru.practicum.explore.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.comment.Comment;
import ru.practicum.explore.model.comment.CommentDto;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.storage.comment.CommentStorage;
import ru.practicum.explore.storage.event.EventStorage;
import ru.practicum.explore.storage.user.UserStorage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentsServiceImpl implements CommentService {
    private final CommentStorage commentStorage;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Override
    @Transactional
    public CommentDto addComment(Comment comment) {
        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пустой запрос");
        }
        CommentDto commentDto = null;
        Event event = eventStorage.findEventById(comment.getEventId());
        User user = userStorage.findUserById(comment.getUserId());
        if (event.getInitiator().equals(user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Инициатор события не оставляет комментарии");
        }
        try {
            commentDto = commentStorage.addComment(comment);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (commentDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Комментарий не добавлен");
        }
        return commentDto;
    }

    @Override
    public CommentDto findCommentById(Long commentId) {
        CommentDto commentDto = null;
        try {
            commentDto = commentStorage.findCommentById(commentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("commentDto " + commentDto);
        if (commentDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Комментарий не найден");
        } else {
            return commentDto;
        }
    }

    @Override
    public List<CommentDto> findCommentsByEventsId(Long eventsId) {
        List<CommentDto> comments = new ArrayList<>();
        try {
            comments = commentStorage.findCommentsByEventsId(eventsId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return comments;
    }
}
