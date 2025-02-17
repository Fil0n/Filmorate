package ru.yandex.practicum.filmrate.storage.user;

import ru.yandex.practicum.filmrate.model.User;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> findAll() throws SQLException;

    User create(User user);

    User update(User newUser);

    void delete(Long userId);

    User read(Long userId) throws SQLException;

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    List<User> getFriends(User user) throws SQLException;

    List<User> getFriendsCommonOther(User user, User otherUser);
}
