package ru.practicum.explore.model.comment;

import lombok.Data;

@Data
public class CommentDto {
    private String text;
    private String eventTitle;
    private String userName;
}
