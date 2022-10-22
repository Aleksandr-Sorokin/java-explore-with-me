package ru.practicum.explore.controller.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.comment.Comment;
import ru.practicum.explore.model.comment.CommentDto;
import ru.practicum.explore.service.comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comment")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto addComment(@RequestBody @Valid Comment comment) {
        return commentService.addComment(comment);
    }

    @GetMapping("/{commentId}")
    public CommentDto findCommentById(@PathVariable @Positive Long commentId) {
        return commentService.findCommentById(commentId);
    }

}
