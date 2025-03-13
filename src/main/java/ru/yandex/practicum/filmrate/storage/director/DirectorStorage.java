package ru.yandex.practicum.filmrate.storage.director;

import ru.yandex.practicum.filmrate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorStorage {


    Director getDirectorById(int id);


    List<Director> getAllDirectors();


    Director createDirector(Director director);


    Director update(Director director);


    void deleteDirectorById(int directorId);

    Set<Director> getDirectorsByFilmId(Long filmId);

}
