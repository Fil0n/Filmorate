package ru.yandex.practicum.filmrate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(Long userId) {
        userStorage.delete(userId);
    }

    public User read(Long userId) {
        return userStorage.read(userId);
    }

    public Collection<User> addFriend(User user, User friend) {
        return userStorage.addFriend(user, friend);
    }

    public void removeFriend(User user, User friend) {
        userStorage.removeFriend(user, friend);
    }
}
