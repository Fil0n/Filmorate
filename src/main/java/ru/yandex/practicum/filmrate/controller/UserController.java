package ru.yandex.practicum.filmrate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findAll() {
        log.info("Получен запрос всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user.toString());
        return userService.create(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя: {}", user.toString());
        return userService.update(user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User read(@PathVariable("id") Long id) {
        log.info("Получен запрос получение пользователя: {}", id);
        return userService.read(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        log.info("Получен запрос удаление пользователя: {}", id);
        userService.delete(id);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable("id") Long userId) {
        log.info("Получен запрос списка друзей пользователя: {}", userId);
        List<User> userFriends = userService.getFriends(userId);
        return userFriends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriendsCommonOther(@PathVariable("id") Long userId,
                                            @PathVariable("otherId") Long otherId) {
        log.info("Вызван метод GET /users/{id}/friends/common/{otherId} с id = {} и otherId = {}", userId, otherId);
        return userService.getFriendsCommonOther(userId, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable("id") Long userId,
                          @PathVariable("friendId") Long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable("id") Long userId,
                             @PathVariable("friendId") Long friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilmRecommendations(@PathVariable("id") Long userId) {
        log.info("Вызван метод GET /users/{id}/recommendations с id = {}", userId);
        return userService.getFilmRecommendations(userId);
    }
}
