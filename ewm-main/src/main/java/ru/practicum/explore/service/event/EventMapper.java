package ru.practicum.explore.service.event;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.explore.model.event.Event;
import ru.practicum.explore.model.event.EventShortDto;
import ru.practicum.explore.service.user.UserMapper;

@Component
public class EventMapper {
    private ModelMapper mapper;
    private UserMapper userMapper;

    public EventMapper(ModelMapper mapper, UserMapper userMapper) {
        this.mapper = mapper;
        this.userMapper = userMapper;
    }

    public EventShortDto toShortDto(Event event) {
        EventShortDto dto = new EventShortDto();
        mapper.map(event, dto);
        dto.setInitiator(userMapper.toUserDto(event.getInitiator()));
        return dto;
    }
}
