package ru.practicum.explore.events.model;

import lombok.Data;

@Data
public class ViewStats {
    String app;
    String uri;
    Integer hits;
}
