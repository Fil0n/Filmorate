package ru.yandex.practicum.filmrate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    public void delete(long id) {
        log.info("Получен запрос на удаление фильма с идентификатором: {}", id);
        filmService.delete(id);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        log.info("Получен запрос на получение популярных фильмо в количестве: {}", count);
        return filmService.getMostPopular(count);
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

}
