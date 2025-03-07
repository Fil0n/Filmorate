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
import ru.yandex.practicum.filmrate.model.*;
import ru.yandex.practicum.filmrate.service.DirectorService;
import ru.yandex.practicum.filmrate.model.*;
import ru.yandex.practicum.filmrate.service.MPAService;
import ru.yandex.practicum.filmrate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmrate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    @Autowired
    private final GenreStorage genreStorage;
    @Autowired
    private final DirectorStorage directorStorage;


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
        updateDirectors(film);
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


    private void updateDirectors(Film film) {
        Set<Director> directors = film.getDirectors();
        if (directors != null) {
            String sqlForDelete = "DELETE from film_director WHERE film_id = ?";
            jdbcTemplate.update(sqlForDelete, film.getId());
            for (Director director : directors) {
                String sql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
                jdbcTemplate.update(sql, film.getId(), director.getId());
            }
        }
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

        jdbcTemplate.update(query, newFilm.getName(), newFilm.getDescription(),
                newFilm.getReleaseDate(), newFilm.getDuration(), newFilm.getMpa().getId(), newFilm.getId());
        if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
            updateFilmGenre(newFilm);
        }
        if (newFilm.getDirectors() != null && !newFilm.getDirectors().isEmpty()) {
            updateDirectors(newFilm);
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
    public Collection<Film> getMostPopular(Integer count) {
        List<Film> films = new ArrayList<>();
        String query = "with l as (select l.film_id, count(l.film_id) likes from film f " +
                "join likes l on f.id = l.film_id " +
                "group by film_id) " +
                "select id, name, description, release_date, mpa, duration from film f " +
                "join l on f.id = l.film_id " +
                "order by l.likes desc " +
                "limit ?";

        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet(query, count);

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

    @Override
    public List<Film> sortFilms(int directorId, String sortBy) {
        String sqlForLikes = "SELECT f.* FROM film AS f "
                + "JOIN film_director AS fd ON f.id = fd.film_id "
                + "JOIN likes ON f.id = likes.film_id "
                + "GROUP BY f.id "
                + "HAVING fd.director_id = ? "
                + "ORDER BY COUNT(likes.film_id) DESC";

        String sqlForYear = "SELECT f.id, f.name, f.mpa, f.description, f.release_date, f.duration FROM film_director AS fd "
                + "LEFT JOIN film AS f ON fd.film_id = f.id "
                + "GROUP BY fd.film_id "
                + "HAVING fd.director_id = ? "
                + "ORDER BY f.release_date";
        if (sortBy.equals("likes")) {
            List<Film> filmsSortedByLikes = jdbcTemplate.query(sqlForLikes, this::mapToFilm, directorId);
            return filmsSortedByLikes;
        } else if (sortBy.equals("year")) {
            List<Film> filmsSortedByYear = jdbcTemplate.query(sqlForYear, this::mapToFilm, directorId);
            return filmsSortedByYear;
        }
        throw new NotFoundException("Измените запрос.");
    }

    private Film mapToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .mpa(mpaService.read(rs.getInt("mpa")))
                .genres(genreStorage.getGenresByFilmId(rs.getLong("id")))
                .directors(directorStorage.getDirectorsByFilmId(rs.getLong("id")))
                .build();
    }
}
