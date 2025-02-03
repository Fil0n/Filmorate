package ru.yandex.practicum.filmrate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.exeption.NotFoundException;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {
        film.setId(Utils.getNextId(films));
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
        }
        films.replace(newFilm.getId(), newFilm);
        return newFilm;
    }
}
