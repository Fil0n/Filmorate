package ru.yandex.practicum.filmrate.storage.film;

import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.User;
import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    void delete(Long filmId);

    Film read(Long filmId);

    Collection<Film> getMostPopular(Integer count);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);
}
