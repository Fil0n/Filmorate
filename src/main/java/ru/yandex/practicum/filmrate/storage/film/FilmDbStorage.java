package ru.yandex.practicum.filmrate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> findAll() throws SQLException {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet("select * from film f");

        while (filmsSet.next()) {
            films.add(rowToFilm((ResultSet) filmsSet));
        }

        return films.stream().collect(Collectors.toList());
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        film.setId((long) simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue());
        updateFilmGenre(film);
        return film;
    }

    public void updateFilmGenre(Film film) {
        deleteFilmGenre(film.getId());

        String query = "insert into film_genre(film_id, genre_id) values (?, ?)";
        film.getGenres().forEach(g -> {
            jdbcTemplate.update(query, film.getId(), g);
        });
    }

    public void deleteFilmGenre(Long filmId) {
        String query = "Delete from film_genre where film_id = ?";
        jdbcTemplate.update(query, filmId);
    }


    @Override
    public Film update(Film newFilm) {
        String query = "update film set " +
                "name = ?, rating = ?, description = ?, releaseDate = ?, duration = ? " +
                "where id = ?";
        int rowsCount = jdbcTemplate.update(query, newFilm.getName(), newFilm.getDescription(),
                newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getRating(), newFilm.getId());
        updateFilmGenre(newFilm);
        return newFilm;
    }

    @Override
    public void delete(Long filmId) {
        deleteFilmGenre(filmId);
        String query = "delete from film where id = ?";
        jdbcTemplate.update(query, filmId);
    }

    @Override
    public Film read(Long filmId) throws SQLException {
        String query = "select * from film where id = ?";
        return rowToFilm(jdbcTemplate.queryForObject(query, ResultSet.class, filmId));
    }

    @Override
    public Collection<Film> getMostPopular(Integer count) throws SQLException {
        List<Film> films = new ArrayList<>();
        String query = "with l as (select l.film_id, count(l.film_id) from film f " +
                "join like l on f.id = l.film_id" +
                "group by film_id) " +
                "select * from film f " +
                "join l on f.id = l.film_id" +
                "limit ?";

        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet("select * from film f");

        while (filmsSet.next()) {
            films.add(rowToFilm((ResultSet) filmsSet));
        }

        return films.stream().collect(Collectors.toList());
    }

    @Override
    public void addLike(Film film, User user) {
        String query = "insert into like (film_id, user_id) values(?, ?)";
        jdbcTemplate.update(query, film.getId(), user.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        String sqlQuery = "delete from like where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
    }

    private Film rowToFilm(ResultSet resultSet) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .rating(resultSet.getInt("rating"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating", film.getRating());
        return values;
    }
}
