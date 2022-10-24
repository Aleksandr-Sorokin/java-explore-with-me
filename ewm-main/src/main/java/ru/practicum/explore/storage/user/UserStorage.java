package ru.practicum.explore.storage.user;

import ru.practicum.explore.model.user.NewUserDto;
import ru.practicum.explore.model.user.User;

import java.util.List;

public interface UserStorage {
    User createUser(NewUserDto user);

    void deleteUser(Long id);

    User findUserById(Long id);

    List<User> findUsers(List<Long> ids, Integer from, Integer size);
}
