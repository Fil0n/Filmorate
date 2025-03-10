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
            String sql = "select director_id, director_name from directors where director_id = ?";
            Director director = jdbcTemplate.queryForObject(sql, directorRowMapper, id);
            return director;
        } catch (Exception e) {
            throw new DataDoNotExistException("Режиссёра с таким id = " + id + " не существует.");
        }
    }

    @Override
    public List<Director> getAllDirectors() {
        String sql = "select director_id, director_name from directors";
        List<Director> directors = jdbcTemplate.query(sql, directorRowMapper);
        return directors;
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "insert into directors (director_name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
                    stmt.setString(1, director.getName());
                    return stmt;
                }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director update(Director director) {
        try {
            getDirectorById(director.getId());
        } catch (Exception e) {
            throw new DataDoNotExistException("Режиссёра с таким id = " + director.getId() + " не существует.");
        }
            String sql = "update directors set director_name = ? where director_id = ?";
            jdbcTemplate.update(sql, director.getName(), director.getId());
            return director;
    }

    @Override
    public void deleteDirectorById(int directorId) {
        String sql = "delete from directors where director_id = ?";
        jdbcTemplate.update(sql, directorId);
    }

    @Override
    public Set<Director> getDirectorsByFilmId(Long filmId) {
        String sql = "select d.director_id, d.director_name from directors as d "
                + "join film_director as fd on d.director_id = fd.director_id "
                + "where fd.film_id = ?";
        List<Director> onlyListOfDirectors = jdbcTemplate.query(sql, directorRowMapper, filmId);
        Set<Director> directors = new HashSet<>(onlyListOfDirectors);
        return directors;
    }

}
