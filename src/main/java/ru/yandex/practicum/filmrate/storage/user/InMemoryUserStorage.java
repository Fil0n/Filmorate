package ru.yandex.practicum.filmrate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.exeption.NotFoundException;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        user.setId(Utils.getNextId(users));
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) throws NotFoundException {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пост с id = " + newUser.getId() + " не найден");
        }

        users.replace(newUser.getId(), newUser);
        return newUser;
    }


}
