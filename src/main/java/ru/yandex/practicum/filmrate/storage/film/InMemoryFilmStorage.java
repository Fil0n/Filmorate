package ru.yandex.practicum.filmrate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.Utils;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<User>> likes = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(Utils.getNextId(films));
        films.put(film.getId(), film);
        likes.put(film.getId(), new HashSet<>());
        return film;
    }

    @Override
    public Film update(Film newFilm) throws NotFoundException {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
        }
        films.replace(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public void delete(Long filmId) {
        films.remove(filmId);
    }

    @Override
    public Film read(Long filmId) {
        return films.get(filmId);
    }

    @Override
    public Collection<Film> getMostPopular(Integer count) {
        return films.values()
                .stream()
                .sorted((film1, film2) -> {
                    int likes1 = likes.get(film1.getId()).size();
                    int likes2 = likes.get(film2.getId()).size();
                    return likes1 != likes2 ? likes2 - likes1 : film1.getId().compareTo(film2.getId());
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Film film, User user) {
        Set<User> likeUsers = likes.get(film.getId());
        likeUsers.add(user);
    }

    @Override
    public void removeLike(Film film, User user) {
        Set<User> likeUsers = likes.get(film.getId());
        likeUsers.remove(user);
    }

    @Override
    public List<Film> sortFilms(int directorId, String sortBy) {
        List<Film> films = new ArrayList<>();
        return films;
    }

}
