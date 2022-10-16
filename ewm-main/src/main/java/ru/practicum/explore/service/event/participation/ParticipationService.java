package ru.practicum.explore.service.event.participation;

import ru.practicum.explore.model.event.participation.ParticipationRequestDto;

import java.util.List;

public interface ParticipationService {
    List<ParticipationRequestDto> findParticipationByEventIdAndUserId(Long userId, Long eventId);

    ParticipationRequestDto confirmParticipation(Long userId, Long eventId, Long reqId);

    ParticipationRequestDto rejectParticipation(Long userId, Long eventId, Long reqId);

    List<ParticipationRequestDto> findAllRequestById(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}