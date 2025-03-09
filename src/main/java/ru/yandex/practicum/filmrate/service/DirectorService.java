package ru.yandex.practicum.filmrate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.Director;
import ru.yandex.practicum.filmrate.model.Genre;
import ru.yandex.practicum.filmrate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmrate.storage.genre.GenreStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DirectorService {

    @Autowired
    private final DirectorStorage directorStorage;

    public Collection<Director> findAll() {
        return directorStorage.getAllDirectors();
    }

    public Director getById(int id) {
        return directorStorage.getDirectorById(id);
    }

    public Director create(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director update(Director director) throws NotFoundException {
        return directorStorage.update(director);
    }

    public void delete (Integer id) {
        directorStorage.deleteDirectorById(id);
    }
}
