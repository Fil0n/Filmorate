package ru.yandex.practicum.filmrate.storage.user;

import ru.yandex.practicum.filmrate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    void delete(Long userId);

    User read(Long userId);

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    List<User> getFriends(User user);

    List<User> getFriendsCommonOther(User user, User otherUser);
}
