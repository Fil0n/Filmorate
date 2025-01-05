package ru.yandex.practicum.filmrate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmrate.exeption.ConditionsNotMetException;
import ru.yandex.practicum.filmrate.exeption.NotFoundException;
import ru.yandex.practicum.filmrate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {

        String validate = validateWithCheckId(newFilm);
        if (!validate.isBlank()) {
            throw new ConditionsNotMetException(validate);
        }

        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
        }

        Film oldUser = films.get(newFilm.getId());
        oldUser = newFilm;
        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private String validateWithCheckId(Film film) {
        if (film.getId() == null) {
            return "Id не должен быть пустым";
        }

        return "";
    }

//    private String validate(Film film) {
//        String error = "";
//        if (film.getName() == null || film.getName().isBlank()){
//            error += "Название не может быть пустым \n";
//        }
//
//        if(film.getDescription().length() > 200) {
//            error += "Максимальная длина описания — 200 символов";
//        }
//
//        LocalDate firstFilmDate = LocalDate.parse("28.12.1885");
//        if(film.getDate().isBefore(firstFilmDate)) {
//            error = "дата релиза — не раньше 28 декабря 1895 года";
//        }
//
//        if(film.getDuration() != null && film.getDuration() < 0) {
//            error += "Продолжительность фильма должна быть положительным числом";
//        }
//
//        return error;
//    }
}
