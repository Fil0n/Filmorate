package ru.yandex.practicum.filmrate.storage.genre;

import ru.yandex.practicum.filmrate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> genres = null;

    Collection<Genre> findAll();

    Genre read(int id);

    Set<Genre> getGenresByFilmId(Long id);

}
