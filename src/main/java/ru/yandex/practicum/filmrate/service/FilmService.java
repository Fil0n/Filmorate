package ru.yandex.practicum.filmrate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.exeption.ExceptionMessages;
import ru.yandex.practicum.filmrate.exeption.NotFoundException;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.storage.film.FilmStorage;
import ru.yandex.practicum.filmrate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
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

    public void delete(Long filmId) {
        filmStorage.delete(filmId);
    }

    public Film read(Long filmId) {
        return filmStorage.read(filmId);
    }

    public Collection<Film> getMostPopular(Integer count) {
        return filmStorage.getMostPopular(count);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = Optional.ofNullable(filmStorage.read(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
        User user = Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, userId)));
        filmStorage.addLike(film, user);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = Optional.ofNullable(filmStorage.read(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
        User user = Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, userId)));
        filmStorage.removeLike(film, user);
    }
}
