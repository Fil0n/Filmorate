package ru.yandex.practicum.filmrate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.exception.ExceptionMessages;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.Genre;
import ru.yandex.practicum.filmrate.model.MPA;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.service.MPAService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private MPAService mpaService;

    @Override
    public Collection<Film> findAll() {
        List<Film> films = new ArrayList<>();
        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet("select id, name, description, release_date, mpa, duration from film");

        while (filmsSet.next()) {
            films.add(mapRowSetToFilm(filmsSet));
        }

        return films;
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
        Set<Genre> genres = film.getGenres();

        if (genres == null) {
            return;
        }

        StringBuilder query = new StringBuilder("insert into film_genre(film_id, genre_id) values ");

        List<Object> params = new ArrayList<>();
        int i = 0;
        for (Genre genre : genres) {
            if (i == 0) {
                query.append("(?, ?)");
            } else {
                query.append(", (?, ?)");
            }

            params.add(film.getId());
            params.add(genre.getId());
            i++;
        }
        jdbcTemplate.update(query.toString(), params.toArray());
    }

    public void deleteFilmGenre(Long filmId) {
        String query = "Delete from film_genre where film_id = ?";
        jdbcTemplate.update(query, filmId);
    }


    @Override
    public Film update(Film newFilm) {
        read(newFilm.getId());

        String query = "update film set " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                "where id = ?";

        if (jdbcTemplate.update(query, newFilm.getName(), newFilm.getDescription(),
                newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId()) > 0) {
            updateFilmGenre(newFilm);
        }

        return newFilm;
    }

    @Override
    public void delete(Long filmId) {
        deleteFilmGenre(filmId);
        String query = "delete from film where id = ?";
        jdbcTemplate.update(query, filmId);
    }

    @Override
    public Film read(Long filmId) {
        String query = "select id, name, description, release_date, mpa, duration from film where id = ?";
        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet(query, filmId);

        while (filmsSet.next()) {
            return mapRowSetToFilm(filmsSet);
        }

        throw new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR, filmId));
    }

    @Override
    public Collection<Film> getMostPopular(Integer count, Integer genreId, Integer year) {
        List<Film> films = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        String query = "WITH l AS (" +
                "SELECT l.film_id, COUNT(l.film_id) AS likes " +
                "FROM likes l " +
                "GROUP BY l.film_id" +
                ") " +
                "SELECT f.id, f.name, f.description, f.release_date, f.mpa, f.duration " +
                "FROM film f " +
                "LEFT JOIN l ON f.id = l.film_id ";

        if (genreId != null) {
            query += "JOIN film_genre g ON f.id = g.film_id AND g.genre_id = ? ";
            params.add(genreId);
        }

        if (year != null) {
            query += "WHERE EXTRACT(YEAR FROM f.release_date) = ? ";
            params.add(year);
        }

        query += "ORDER BY l.likes DESC ";

        if (count != null) {
            query += "LIMIT ?";
            params.add(count);
        }

        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet(query, params.toArray());

        while (filmsSet.next()) {
            films.add(mapRowSetToFilm(filmsSet));
        }

        return films;
    }

    @Override
    public void addLike(Film film, User user) {
        String query = "insert into likes (film_id, user_id) values(?, ?)";
        jdbcTemplate.update(query, film.getId(), user.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
    }

    public Film mapRowSetToFilm(SqlRowSet rowSet) {
        MPA mpa = mpaService.findAll().stream()
                .filter(m -> m.getId() == rowSet.getInt("mpa"))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.MPA_NOT_FOUND_ERROR, rowSet.getInt("mpa"))));

        return Film.builder()
                .id(rowSet.getLong("id"))
                .name(rowSet.getString("name"))
                .description(rowSet.getString("description"))
                .releaseDate(rowSet.getDate("release_date") == null ? null : rowSet.getDate("release_date").toLocalDate())
                .duration(rowSet.getInt("duration"))
                .mpa(mpa)
                .build();
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa", film.getMpa().getId());
        return values;
    }
}
