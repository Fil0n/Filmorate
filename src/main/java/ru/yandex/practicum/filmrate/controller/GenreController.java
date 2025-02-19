package ru.yandex.practicum.filmrate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmrate.model.Genre;
import ru.yandex.practicum.filmrate.service.GenreSevice;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    @Autowired
    private final GenreSevice genreSevice;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Genre> findAll() {
        log.info("Получен запрос получение списка всех жанров");
        return genreSevice.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Genre read(@PathVariable("id") int id) {
        log.info("Получен запрос на получение жанра с идентификатором: {}", id);
        return genreSevice.read(id);
    }
}
