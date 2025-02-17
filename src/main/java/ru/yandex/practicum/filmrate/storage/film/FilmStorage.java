package ru.yandex.practicum.filmrate.storage.film;

import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.User;

import java.sql.SQLException;
import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll() throws SQLException;

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Long filmId);

    Film read(Long filmId) throws SQLException;

    Collection<Film> getMostPopular(Integer count) throws SQLException;

    void addLike(Film film, User user);

    void removeLike(Film film, User user);
}
