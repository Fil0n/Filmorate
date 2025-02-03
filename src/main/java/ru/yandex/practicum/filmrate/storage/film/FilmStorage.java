package ru.yandex.practicum.filmrate.storage.film;

import ru.yandex.practicum.filmrate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> findAll();
}
