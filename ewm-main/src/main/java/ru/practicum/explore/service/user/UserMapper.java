package ru.practicum.explore.service.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.explore.model.user.NewUserDto;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.model.user.UserDto;

@Component
public class UserMapper {
    private ModelMapper mapper;

    public UserMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public User toEntity(NewUserDto newUserDto) {
        if (newUserDto == null) {
            return null;
        } else {
            return mapper.map(newUserDto, User.class);
        }
    }

    public UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        mapper.map(user, dto);
        return dto;
    }
}
