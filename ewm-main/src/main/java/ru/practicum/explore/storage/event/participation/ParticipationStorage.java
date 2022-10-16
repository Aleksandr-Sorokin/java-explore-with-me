package ru.practicum.explore.storage.event.participation;

import ru.practicum.explore.enums.Status;
import ru.practicum.explore.model.event.participation.ParticipationRequestDto;

import java.util.List;

public interface ParticipationStorage {
    List<ParticipationRequestDto> findParticipationByEventIdAndUserId(Long userId, Long eventId);

    ParticipationRequestDto confirmParticipation(Long userId, Long eventId, Long reqId, Status status);

    ParticipationRequestDto rejectParticipation(Long userId, Long eventId, Long reqId);

    List<ParticipationRequestDto> findAllRequestById(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId, Status status);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}