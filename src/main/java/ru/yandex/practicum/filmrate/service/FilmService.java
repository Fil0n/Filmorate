package ru.yandex.practicum.filmrate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.exeption.NotFoundException;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.storage.film.FilmStorage;
import ru.yandex.practicum.filmrate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) throws NotFoundException {
        return filmStorage.update(film);
    }

    public void delete(Long filmId){
        filmStorage.delete(filmId);
    }

    public Film read(Long filmId){
        return filmStorage.read(filmId);
    }

    public Collection<Film> getMostPopular(Integer count){
        return filmStorage.getMostPopular(count);
    }
}
