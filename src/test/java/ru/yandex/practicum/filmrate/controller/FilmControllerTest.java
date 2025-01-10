package ru.yandex.practicum.filmrate.controller;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmrate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmControllerTest {

    private FilmController controller;
    private Film film;

    @BeforeAll
    void init() {
        controller = new FilmController();
        film = new Film("FilmName", LocalDate.of(2000, 1, 1));
    }

    @Test
    void createFilm() {
        Film expectedFilm = controller.create(film);
        assertEquals(film.getName(), expectedFilm.getName(), "Имя не совпадает");
        assertEquals(film.getReleaseDate().toString(), expectedFilm.getReleaseDate().toString(), "Дата не совпадает");
    }

    @Test
    void updateFilm() {
        film.setDuration(30);
        Film expectedFilm = controller.update(film);
        assertEquals(film.getName(), expectedFilm.getName(), "Имя не совпадает");
        assertEquals(film.getReleaseDate().toString(), expectedFilm.getReleaseDate().toString(), "Дата не совпадает");
        assertEquals(film.getReleaseDate().toString(), expectedFilm.getReleaseDate().toString(), "Дата не совпадает");
    }
}