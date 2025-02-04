package ru.yandex.practicum.filmrate.storage.user;

import ru.yandex.practicum.filmrate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    void delete(Long userId);

    User read(Long userId);

    Collection<User> addFriend(User user, User friend);

    void removeFriend(User user, User friend);
}
