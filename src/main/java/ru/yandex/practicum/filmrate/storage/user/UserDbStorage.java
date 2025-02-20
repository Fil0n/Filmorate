package ru.yandex.practicum.filmrate.storage.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.exception.ExceptionMessages;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> findAll() {
        List<User> users = new ArrayList<>();
        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet("select id, email, login, name, birthday from users");

        while (filmsSet.next()) {
            users.add(mapRowSetToUser(filmsSet));
        }

        return users.stream().collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        isMatch(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        user.setId((long) simpleJdbcInsert.executeAndReturnKey(toMap(user)).intValue());
        return user;
    }

    @Override
    public User update(User newUser) {
        read(newUser.getId());
        isMatch(newUser);

        String query = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(query, newUser.getEmail(), newUser.getLogin(),
                newUser.getName(), newUser.getBirthday(), newUser.getId());
        return newUser;
    }

    @Override
    public void delete(Long userId) {
        removeAllFriends(userId);
        String query = "delete from users where id = ?";
        jdbcTemplate.update(query, userId);
    }

    private void removeAllFriends(Long userId) {
        String query = "delete from friendship where user_id = ? or friend_id = ?";
        jdbcTemplate.update(query, userId, userId);
    }

    @Override
    public User read(Long userId) {
        String query = "select id, email, login, name, birthday from users where id = ?";
        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet(query, userId);

        while (filmsSet.next()) {
            return mapRowSetToUser(filmsSet);
        }

        throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId));
    }

    public void isMatch(User user) {
        String query = "select id, email, login, name, birthday from users where email = ?";
        SqlRowSet filmsSet;
        if (user.getId() == null) {
            filmsSet = jdbcTemplate.queryForRowSet(query, user.getEmail());
        } else {
            query += " and id != ?";
            filmsSet = jdbcTemplate.queryForRowSet(query, user.getEmail(), user.getId());
        }

        while (filmsSet.next()) {
            throw new ValidationException("Этот адрес электронной почты уже используется");
        }
    }

    @Override
    public void addFriend(User user, User friend) {
        String query = "insert into friendship (user_id, friend_id) values(?, ?)";
        jdbcTemplate.update(query, user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(User user, User friend) {
        String query = "delete from friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(query, user.getId(), friend.getId());
    }

    @Override
    public List<User> getFriends(User user) {
        List<User> users = new ArrayList<>();
        String query = "select id, email, login, name, birthday from users where id in (select friend_id from friendship f where f.user_id = ?)";
        SqlRowSet usersSet = jdbcTemplate.queryForRowSet(query, user.getId());

        while (usersSet.next()) {
            users.add(mapRowSetToUser(usersSet));
        }

        return users;
    }

    @Override
    public List<User> getFriendsCommonOther(User user, User otherUser) {
        List<User> users = new ArrayList<>();
        String query = "select id, email, login, name, birthday from users where id in (select friend_id from friendship f where f.user_id = ?) and id in(select friend_id from friendship f where f.user_id = ?)";
        SqlRowSet usersSet = jdbcTemplate.queryForRowSet(query, user.getId(), otherUser.getId());

        while (usersSet.next()) {
            users.add(mapRowSetToUser(usersSet));
        }

        return users;
    }

    public User mapRowSetToUser(SqlRowSet rowSet) {
        return User.builder()
                .id(rowSet.getLong("id"))
                .email(rowSet.getString("email"))
                .login(rowSet.getString("login"))
                .name(rowSet.getString("name"))
                .birthday(rowSet.getDate("birthday").toLocalDate())
                .build();
    }

    private Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }
}
