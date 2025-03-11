package ru.yandex.practicum.filmrate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    @Autowired
    private final FilmService filmService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findAll() {
        log.info("Получен запрос получение списка всех фильмов");
        return filmService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма: {}", film.toString());
        return filmService.create(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос на изменение фильма: {}", film.toString());
        return filmService.update(film);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id) {
        log.info("Получен запрос на удаление фильма с идентификатором: {}", id);
        filmService.delete(id);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getPopularFilms(@RequestParam(required = false) Integer count,
                                            @RequestParam(required = false) Integer genreId,
                                            @RequestParam(required = false) Integer year) {
        log.info("Получен запрос на получение популярных фильмов");
        return filmService.getMostPopular(count, genreId, year);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable("id") Long filmId,
                        @PathVariable("userId") Long userId) {
        log.info("Получен запрос на добавление лайка фильма с id = {} пользователем с шв = {}", filmId, userId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@PathVariable("id") Long filmId,
                           @PathVariable("userId") Long userId) {
        log.info("Получен запрос на удаление лайка фильма с id = {} пользователем с шв = {}", filmId, userId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film read(@PathVariable("id") long id) {
        log.info("Получен запрос на получение фильма с идентификатором: {}", id);
        return filmService.read(id);
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(
            @RequestParam long userId,
            @RequestParam long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
