package ru.practicum.explore.model.views;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class EndpointHit {
    Long id;
    @NotNull
    @Size(max = 2000)
    private String app;
    @NotNull
    @Size(max = 2000)
    private String uri;
    @NotNull
    @Size(max = 250)
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
