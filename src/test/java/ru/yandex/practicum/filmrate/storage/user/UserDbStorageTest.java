package ru.yandex.practicum.filmrate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import jakarta.validation.ValidationException;
import ru.yandex.practicum.filmrate.model.User;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {
    @Autowired
    private UserDbStorage userDbStorage;

    private User user;
    private User friend;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@example.com")
                .login("testLogin")
                .name("Test User")
                .birthday(Date.valueOf("1990-01-01").toLocalDate())
                .build();

        friend = User.builder()
                .email("friend@example.com")
                .login("friendLogin")
                .name("Friend User")
                .birthday(Date.valueOf("1995-05-05").toLocalDate())
                .build();
    }

    @Test
    void testCreateAndFindAll() {
        User createdUser = userDbStorage.create(user);
        assertNotNull(createdUser);
        assertEquals(1L, createdUser.getId());

        Collection<User> users = userDbStorage.findAll();
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void testUpdate() {
        User createdUser = userDbStorage.create(user);
        createdUser.setName("Updated User Name");

        User updatedUser = userDbStorage.update(createdUser);
        assertNotNull(updatedUser);
        assertEquals("Updated User Name", updatedUser.getName());

        User foundUser = userDbStorage.read(createdUser.getId());
        assertEquals("Updated User Name", foundUser.getName());
    }

    @Test
    void testDelete() {
        User createdUser = userDbStorage.create(user);
        userDbStorage.delete(createdUser.getId());

        assertThrows(NotFoundException.class, () -> userDbStorage.read(createdUser.getId()));
    }

    @Test
    void testRead() {
        User createdUser = userDbStorage.create(user);
        User foundUser = userDbStorage.read(createdUser.getId());

        assertNotNull(foundUser);
        assertEquals(createdUser, foundUser);
    }

    @Test
    void testReadNotFound() {
        assertThrows(NotFoundException.class, () -> userDbStorage.read(999L));
    }

    @Test
    void testIsMatchValidationException() {
        userDbStorage.create(user);

        User duplicateUser = User.builder()
                .email("test@example.com")
                .login("anotherLogin")
                .name("Another User")
                .birthday(Date.valueOf("1990-01-01").toLocalDate())
                .build();

        assertThrows(ValidationException.class, () -> userDbStorage.create(duplicateUser));
    }

    @Test
    void testAddAndRemoveFriend() {
        User createdUser = userDbStorage.create(user);
        User createdFriend = userDbStorage.create(friend);

        userDbStorage.addFriend(createdUser, createdFriend);
        List<User> friends = userDbStorage.getFriends(createdUser);
        assertEquals(1, friends.size());
        assertEquals(createdFriend, friends.get(0));

        userDbStorage.removeFriend(createdUser, createdFriend);
        friends = userDbStorage.getFriends(createdUser);
        assertEquals(0, friends.size());
    }

    @Test
    void testGetCommonFriends() {
        User createdUser = userDbStorage.create(user);
        User createdFriend = userDbStorage.create(friend);
        User commonFriend = User.builder()
                .email("common@example.com")
                .login("commonLogin")
                .name("Common Friend")
                .birthday(Date.valueOf("1992-02-02").toLocalDate())
                .build();
        userDbStorage.create(commonFriend);

        userDbStorage.addFriend(createdUser, commonFriend);
        userDbStorage.addFriend(createdFriend, commonFriend);

        List<User> commonFriends = userDbStorage.getFriendsCommonOther(createdUser, createdFriend);
        assertEquals(1, commonFriends.size());
        assertEquals(commonFriend, commonFriends.get(0));
    }
}
