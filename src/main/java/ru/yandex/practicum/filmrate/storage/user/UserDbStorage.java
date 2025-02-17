package ru.yandex.practicum.filmrate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet("select * from film");

        while (filmsSet.next()) {
            users.add(rowToUser((ResultSet) filmsSet));
        }

        return users.stream().collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user")
                .usingGeneratedKeyColumns("id");
        user.setId((long) simpleJdbcInsert.executeAndReturnKey(toMap(user)).intValue());
        return user;
    }

    @Override
    public User update(User newUser) {
        String query = "update user set " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "where id = ?";
        int rowsCount = jdbcTemplate.update(query, newUser.getEmail(), newUser.getLogin(),
                newUser.getName(), newUser.getBirthday(), newUser.getId());
        return newUser;
    }

    @Override
    public void delete(Long userId) {
        removeAllFriends(userId);
        String query = "delete from user where id = ?";
        jdbcTemplate.update(query, userId);
    }

    private void removeAllFriends(Long userId) {
        String query = "delete from friendship where user_id = ? or friend_id = ?";
        jdbcTemplate.update(query, userId, userId);
    }

    @Override
    public User read(Long userId) throws SQLException {
        String query = "select * from user where id = ?";
        return rowToUser(jdbcTemplate.queryForObject(query, ResultSet.class, userId));
    }

    @Override
    public void addFriend(User user, User friend) {
        String query = "insert into friendship (user_id, friend_id) values(?, ?)";
        jdbcTemplate.update(query, user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(User user, User friend) {
        String query = "delete from friendship where user_id = ?, friend_id = ?";
        jdbcTemplate.update(query, user.getId(), friend.getId());
    }

    @Override
    public List<User> getFriends(User user) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "select * from user where id in(select friend_id from friendship f where f.user_id = ?)";
        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet(query);
        while (filmsSet.next()) {
            users.add(rowToUser((ResultSet) filmsSet));
        }

        return users;
    }

    @Override
    public List<User> getFriendsCommonOther(User user, User otherUser) {
        return null;
    }

    private User rowToUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
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
