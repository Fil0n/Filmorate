package ru.yandex.practicum.filmrate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmrate.model.MPA;
import ru.yandex.practicum.filmrate.service.MPAService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MPAController {
    @Autowired
    private final MPAService mpaService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<MPA> findAll() {
        log.info("Получен запрос получение списка всех жанров");
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MPA read(@PathVariable("id") int id) {
        log.info("Получен запрос на получение жанра с идентификатором: {}", id);
        return mpaService.read(id);
    }
}
