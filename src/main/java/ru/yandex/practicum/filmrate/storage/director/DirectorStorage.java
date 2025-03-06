package ru.yandex.practicum.filmrate.storage.director;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.Director;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface DirectorStorage {


    Director getDirectorById(int id);


    List<Director> getAllDirectors();


    Director createDirector(Director director);


    Director update(Director director);


    void deleteDirectorById(int directorId);
    Set<Director> getDirectorsByFilmId (Long filmId);

}
