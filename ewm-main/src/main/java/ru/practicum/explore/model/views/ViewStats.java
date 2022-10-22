package ru.practicum.explore.model.views;

import lombok.Data;

@Data
public class ViewStats {
    private String app;
    private String uri;
    private Integer hits;
}
