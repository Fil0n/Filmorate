package ru.yandex.practicum.filmrate.storage.user;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.Utils;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.exception.ExceptionMessages;
import ru.yandex.practicum.filmrate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<User>> friends = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {

        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            log.error("Адрес электронной почты {} уже используется", user.getEmail());
            throw new ValidationException("Этот адрес электронной почты уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пользователя не указано, используется логин ({}) в качестве имени", user.getLogin());
            user.setName(user.getLogin());
        }

        user.setId(Utils.getNextId(users));
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User update(User user) throws NotFoundException {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, user.getId()));
        }

        if (users.values().stream()
                .anyMatch(u -> !Objects.equals(u.getId(), user.getId()) && u.getEmail().equals(user.getEmail()))) {
            log.error("Адрес электронной почты {} уже используется", user.getEmail());
            throw new ValidationException("Этот адрес электронной почты уже используется");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя пользователя не указано, используется логин ({}) в качестве имени", user.getLogin());
            user.setName(user.getLogin());
        }

        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        User user = users.get(userId);
        for (User friend : friends.get(userId)) {
            removeFriend(user, friend);
        }
        users.remove(userId);
    }

    @Override
    public User read(Long userId) {
        return users.get(userId);
    }

    @Override
    public void addFriend(User user, User friend) {
        Set<User> userFriends = friends.get(friend.getId());
        userFriends.add(user);

        userFriends = friends.get(user.getId());
        userFriends.add(friend);
    }

    @Override
    public void removeFriend(User user, User friend) {
        Set<User> userFriends = friends.get(friend.getId());
        userFriends.remove(user);

        userFriends = friends.get(user.getId());
        userFriends.remove(friend);
    }

    @Override
    public List<User> getFriends(User user) {
        return new ArrayList<>(friends.get(user.getId()));
    }

    @Override
    public List<User> getFriendsCommonOther(User user, User otherUser) {
        final Set<User> userFriends = friends.get(user.getId());
        final Set<User> otherUserFriends = friends.get(otherUser.getId());

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .toList();
    }

}
