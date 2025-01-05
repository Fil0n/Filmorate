package ru.yandex.practicum.filmrate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmrate.exeption.ConditionsNotMetException;
import ru.yandex.practicum.filmrate.exeption.NotFoundException;
import ru.yandex.practicum.filmrate.model.User;

import java.time.LocalDate;
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
//        String validate = validateWithCheckId(user);
//        if (!validate.isBlank()) {
//            throw new ConditionsNotMetException(validate);
//        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Пытаемся обновить пользователя пользователя", newUser);
        String validate = validateWithCheckId(newUser);
        if (!validate.isBlank()) {
            throw new ConditionsNotMetException(validate);
        }

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

//    private String validate(User user) {
//        String error = "";
//        log.info("Валидация логина пользователя");
//        if (user.getLogin() == null || user.getLogin().contains(" ")){
//            log.error("Логин пользователя (%s) пустой либо содержит пробелы.", user.getLogin());
//            error += "Логин не может быть пустым и содержать пробелы \n";
//        }
//
//        log.info("Валидация имени пользователя пользователя");
//        if(user.getName() == null || user.getName().isBlank() || user.getName().isEmpty() && error.isBlank()) {
//            log.info("Имя пользователя не найдено и замененно на логин");
//            user.setName(user.getLogin());
//        }
//
//        log.info("Валидация даты рождения пользователя");
//        if(user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())){
//            log.error("Дата рождения (%s) должна быть меньше текущей даты", user.getBirthday());
//            error += "Дата рождения не может быть в будущем";
//        }
//
//        return error;
//    }

}
