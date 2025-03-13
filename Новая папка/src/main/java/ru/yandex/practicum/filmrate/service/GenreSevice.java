package ru.yandex.practicum.filmrate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.model.Genre;
import ru.yandex.practicum.filmrate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreSevice {
    @Autowired
    private GenreStorage genreStorage;

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre read(int id) {
        return genreStorage.read(id);
    }

    public Set<Genre> getGenresByFilmId(Long id) {
        return genreStorage.getGenresByFilmId(id);
    }
}
