package ru.yandex.practicum.filmrate.storage.director;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmrate.exception.DataDoNotExistException;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.Director;

import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Slf4j
@RequiredArgsConstructor
@Repository
public class DirectorDBStorage implements DirectorStorage{

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private final DirectorRowMapper directorRowMapper;

    @Override
    public Director getDirectorById(int id) {
        try {
            String sql = "SELECT * FROM directors WHERE director_id = ?";
            Director director = jdbcTemplate.queryForObject(sql, directorRowMapper, id);
            return director;
        } catch (Exception e) {
            throw new DataDoNotExistException("Режиссёра с таким id = " + id + " не существует.");
        }
    }

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM directors";
        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper);
        return directors;
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO directors (director_name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
                    stmt.setString(1, director.getName());
                    return stmt;
                }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        Director addedDirector = getDirectorById(director.getId());
        return addedDirector;
    }

    @Override
    public Director update(Director director) {
        if (doesDirectorExist(director.getId())) {
            String sql = "UPDATE directors SET director_name = ? WHERE director_id = ?";
            jdbcTemplate.update(sql, director.getName(), director.getId());
            Director thisdirector = getDirectorById(director.getId());
            return thisdirector;
        } else {
            throw new NotFoundException("Режиссёра с таким " + director.getId() + " не существует.");
        }
    }

    @Override
    public void deleteDirectorById(int directorId) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, directorId);
        String sqlForFilms = "DELETE FROM film_director WHERE director_id = ?";
        jdbcTemplate.update(sqlForFilms, directorId);
    }

    private boolean doesDirectorExist(Integer directorId) {
        try {
            getDirectorById(directorId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }

    @Override
    public Set<Director> getDirectorsByFilmId (Long filmId) {
        String sql = "SELECT d.* FROM directors AS d "
                + "JOIN film_director AS fd ON d.director_id = fd.director_id "
                + "WHERE fd.film_id = ?";
        List<Director> onlyListOfDirectors = jdbcTemplate.query(sql, directorRowMapper, filmId);
        Set<Director> directors = new HashSet<>(onlyListOfDirectors);
        return directors;
    }

}
