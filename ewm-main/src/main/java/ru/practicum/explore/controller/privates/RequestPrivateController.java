package ru.practicum.explore.controller.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.event.participation.ParticipationRequestDto;
import ru.practicum.explore.service.event.participation.ParticipationService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestPrivateController {
    private final ParticipationService participationService;

    @GetMapping
    public List<ParticipationRequestDto> findAllRequestById(@PathVariable @Positive Long userId) {
        return participationService.findAllRequestById(userId);
    }

    @PostMapping
    public ParticipationRequestDto createRequest(@PathVariable @Positive Long userId,
                                                 @RequestParam @Positive Long eventId) {
        return participationService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long requestId) {
        return participationService.cancelRequest(userId, requestId);
    }
}
