package ru.yandex.practicum.filmrate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.exception.ExceptionMessages;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.EventType;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.Genre;
import ru.yandex.practicum.filmrate.model.Operation;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmrate.storage.film.FilmStorage;
import ru.yandex.practicum.filmrate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private MPAService mpaService;
    @Autowired
    private GenreSevice genreSevice;
    @Autowired
    private final DirectorStorage directorStorage;
    @Autowired
    private FeedService feedService;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        updateMPAAndGenre(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) throws NotFoundException {
        updateMPAAndGenre(film);
        return filmStorage.update(film);
    }

    private void updateMPAAndGenre(Film film) {
        if (film.getGenres() != null) {
            Set<Genre> filmGenres = new HashSet<>(film.getGenres());
            Set<Genre> newGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
            filmGenres.forEach(g -> {
                newGenres.add(genreSevice.findAll().stream()
                        .filter(o -> o.getId() == g.getId())
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.GENRE_NOT_FOUND_ERROR, g.getId()))));
            });

            film.setGenres(newGenres);
        }

        if (film.getMpa() != null) {
            film.setMpa(mpaService.findAll().stream()
                    .filter(o -> o.getId() == film.getMpa().getId())
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.MPA_NOT_FOUND_ERROR, film.getMpa().getId()))));
        }
    }

    public void delete(Long filmId) {
        filmStorage.delete(filmId);
    }

    public Film read(Long filmId) {
        Film film = filmStorage.read(filmId);

        film.setGenres(genreSevice.getGenresByFilmId(filmId));
        film.setDirectors(directorStorage.getDirectorsByFilmId(filmId));
        return film;
    }

    public Collection<Film> getMostPopular(Integer count, Integer genreId, Integer year) {
        if (genreId != null) {
            genreSevice.read(genreId);
        }

        return filmStorage.getMostPopular(count, genreId, year);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = Optional.ofNullable(filmStorage.read(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
        User user = Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, userId)));
        filmStorage.addLike(film, user);
        feedService.create(EventType.LIKE, Operation.ADD, userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = Optional.ofNullable(filmStorage.read(filmId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId)));
        User user = Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, userId)));
        filmStorage.removeLike(film, user);
        feedService.create(EventType.LIKE, Operation.REMOVE, userId, filmId);
    }

    public Collection<Film> search(String query, Set<String> by) {
        return filmStorage.search(query, by);
    }

    public List<Film> getSortedFilms(int directorId, String sortBy) {
        return filmStorage.sortFilms(directorId, sortBy);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        User user = Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        User friend = Optional.ofNullable(userStorage.read(friendId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, friendId)));
        return filmStorage.getCommonFilms(userId,friendId);
    }
}
