package ru.yandex.practicum.filmrate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmrate.model.Director;
import ru.yandex.practicum.filmrate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    @Autowired
    private final DirectorService directorService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Director> findAll() {
        log.info("Получен запрос получение списка всех режиссёров.");
        return directorService.findAllDirectors();
    }
    @GetMapping("/{id}")
    public Director returnDirectorById(@PathVariable Integer id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@RequestBody Director director) {
        log.info("Получен запрос на добавление режиссёра: {}", director.toString());
        return directorService.create(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director update(@RequestBody Director director) {
        log.info("Получен запрос на изменение режиссёра: {}", director.toString());
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Integer id) {
        log.info("Получен запрос на удаление режиссёра с идентификатором: {}", id);
        directorService.delete(id);
    }



}
