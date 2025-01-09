package ru.yandex.practicum.filmrate.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmrate.model.User;

import java.time.LocalDate;

@SpringBootTest
class UserControllerTest {
    private UserController controller;
    private User user;

    @BeforeAll
    void init() {
        controller = new UserController();
        User user = new User("test@test.ru", "login");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void createUser() {
        User newUser = controller.create(user);
        Assert.assertEquals(user.getName(), newUser.getName(), "Имя не совпадает");
        Assert.assertEquals(user.getLogin(), newUser.getLogin(), "Логин не совпадает");
        Assert.assertEquals(user.getEmail(), newUser.getEmail(), "Почта не совпадает");
    }

    @Test
    void updateUser() {
        user.setName("userName");
        User newUser = controller.update(user);

        Assert.assertEquals(user.getName(), newUser.getName(), "Имя не совпадает");
        Assert.assertEquals(user.getLogin(), newUser.getLogin(), "Логин не совпадает");
        Assert.assertEquals(user.getEmail(), newUser.getEmail(), "Почта не совпадает");
    }
}