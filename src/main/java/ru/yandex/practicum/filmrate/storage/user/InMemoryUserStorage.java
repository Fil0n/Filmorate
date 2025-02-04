package ru.yandex.practicum.filmrate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.exeption.NotFoundException;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.Utils;

import java.util.*;
import java.util.stream.Collectors;

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
        user.setId(Utils.getNextId(users));
        users.put(user.getId(), user);
        friends.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User update(User newUser) throws NotFoundException {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пост с id = " + newUser.getId() + " не найден");
        }

        users.replace(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void delete(Long userId) {
        User user = users.get(userId);
        for(User friend : friends.get(userId)){
            removeFriend(user, friend);
        }
        users.remove(userId);
    }

    @Override
    public User read(Long userId){
        return users.get(userId);
    }

    @Override
    public Collection<User> addFriend(User user, User friend) {
        Set<User> userFriends = friends.get(friend.getId());
        userFriends.add(user);

        userFriends = friends.get(user.getId());
        userFriends.add(friend);

        return userFriends.stream().collect(Collectors.toList());
    }

    @Override
    public void removeFriend(User user, User friend) {
        Set<User> userFriends = friends.get(friend.getId());
        userFriends.remove(user);

        userFriends = friends.get(user.getId());
        userFriends.remove(friend);
    }

}
