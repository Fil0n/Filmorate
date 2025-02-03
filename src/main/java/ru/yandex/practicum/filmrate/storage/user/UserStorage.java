package ru.yandex.practicum.filmrate.storage.user;

import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);


}
