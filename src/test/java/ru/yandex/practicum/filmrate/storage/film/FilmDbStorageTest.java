package ru.yandex.practicum.filmrate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.storage.user.UserDbStorage;

import java.sql.Date;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;
    @Autowired
    private UserDbStorage userDbStorage;

    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(Date.valueOf("2020-01-01").toLocalDate())
                .duration(120)
                .build();

        user = User.builder()
                .email("test@example.com")
                .login("testLogin")
                .name("Test User")
                .birthday(Date.valueOf("1990-01-01").toLocalDate())
                .build();
    }

    @Test
    void testCreateAndFindAll() {
        Film createdFilm = filmDbStorage.create(film);
        assertNotNull(createdFilm);
        assertEquals(1L, createdFilm.getId());

        Collection<Film> films = filmDbStorage.findAll();
        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals(createdFilm, films.iterator().next());
    }

    @Test
    void testUpdate() {
        Film createdFilm = filmDbStorage.create(film);
        createdFilm.setDescription("Updated Film Description");

        Film updatedFilm = filmDbStorage.update(createdFilm);
        assertNotNull(updatedFilm);
        assertEquals("Updated Film Description", updatedFilm.getDescription());

        Film foundFilm = filmDbStorage.read(createdFilm.getId());
        assertEquals("Updated Film Description", foundFilm.getDescription());
    }

    @Test
    void testDelete() {
        Film createdFilm = filmDbStorage.create(film);
        filmDbStorage.delete(createdFilm.getId());

        assertThrows(NotFoundException.class, () -> filmDbStorage.read(createdFilm.getId()));
    }

    @Test
    void testRead() {
        Film createdFilm = filmDbStorage.create(film);
        Film foundFilm = filmDbStorage.read(createdFilm.getId());

        assertNotNull(foundFilm);
        assertEquals(createdFilm, foundFilm);
    }

    @Test
    void testGetMostPopular() {
        Film createdFilm = filmDbStorage.create(film);
        User createdUser = userDbStorage.create(user);
        filmDbStorage.addLike(createdFilm, createdUser);

        Collection<Film> popularFilms = filmDbStorage.getMostPopular(1);
        assertNotNull(popularFilms);
        assertEquals(1, popularFilms.size());
    }

    @Test
    void testAddAndRemoveLike() {
        Film createdFilm = filmDbStorage.create(film);
        User createdUser = userDbStorage.create(user);
        filmDbStorage.addLike(createdFilm, createdUser);

        Collection<Film> popularFilms = filmDbStorage.getMostPopular(1);
        assertEquals(1, popularFilms.size());

        filmDbStorage.removeLike(createdFilm, user);
        popularFilms = filmDbStorage.getMostPopular(1);
        assertEquals(0, popularFilms.size());
    }
}
