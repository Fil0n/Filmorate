package ru.yandex.practicum.filmrate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmrate.exeption.NotFoundException;
import ru.yandex.practicum.filmrate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Пытаемся получить список пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Пытаемся добавить пользователя", user);
        validate(user);

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Пытаемся обновить пользователя пользователя", newUser);
        validate(newUser);

        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пост с id = " + newUser.getId() + " не найден");
        }

        User oldUser = users.get(newUser.getId());
        oldUser = newUser;
        log.info("Пользователь обновлен", newUser.getId());
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private String validateWithCheckId(User user) {
        log.info("Валидация доступности пользоватля с Id");
        if (user.getId() == null) {
            log.error("Пользователь с идентификатором %d не найден", user.getId());
            return "Id не должен быть пустым";
        }

        return "";
    }

    public void validate(User user) {
        log.info("User id = {}", user.getId());
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя для отображения пустое — используем использован логин : {}", user.getLogin());
            user.setName(user.getLogin());
        }

    }

}
