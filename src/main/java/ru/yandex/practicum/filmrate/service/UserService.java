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
}
