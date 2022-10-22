package ru.practicum.explore.controller.admins;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.explore.model.user.NewUserDto;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.service.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody NewUserDto userDto) {
        if (userDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Данные о пользователе не может быть пустым");
        }
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<User> findUsers(@RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam List<Long> ids) {
        return userService.findUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Positive Long userId) {
        userService.deleteUser(userId);
    }
}
