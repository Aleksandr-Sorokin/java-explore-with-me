package ru.practicum.explore.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.model.user.NewUserDto;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.storage.user.UserStorage;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    @Transactional
    public User createUser(NewUserDto user) {
        return userStorage.createUser(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    @Override
    public User findUserById(Long id) {
        return userStorage.findUserById(id);
    }

    @Override
    public List<User> findUsers(List<Long> ids, Integer from, Integer size) {
        return userStorage.findUsers(ids, from, size);
    }
}
