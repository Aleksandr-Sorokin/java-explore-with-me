package ru.practicum.explore.controller.admins;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.model.user.NewUserDto;
import ru.practicum.explore.model.user.User;
import ru.practicum.explore.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserAdminController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody NewUserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<User> findUsers(@RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "0") int from,
                                @RequestParam List<Long> ids) {
        return userService.findUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
