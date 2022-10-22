package ru.practicum.explore.controller.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore.model.comment.CommentDto;
import ru.practicum.explore.service.comment.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comment")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/event/{eventId}")
    public List<CommentDto> findCommentsByEventsId(@PathVariable Long eventId) {
        return commentService.findCommentsByEventsId(eventId);
    }
}
