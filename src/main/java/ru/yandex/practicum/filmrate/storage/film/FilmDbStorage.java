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
import ru.yandex.practicum.filmrate.helper.BinarySlopeOne;
import ru.yandex.practicum.filmrate.model.Film;
import ru.yandex.practicum.filmrate.model.Genre;
import ru.yandex.practicum.filmrate.model.MPA;
import ru.yandex.practicum.filmrate.model.User;
import ru.yandex.practicum.filmrate.service.MPAService;
import ru.yandex.practicum.filmrate.storage.genre.GenreStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

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
    public Collection<Film> getMostPopular(Integer count) {
        List<Film> films = new ArrayList<>();
        String query = "with l as (select l.film_id, count(l.film_id) likes from film f " +
                "join likes l on f.id = l.film_id " +
                "group by film_id) " +
                "select id, name, description, release_date, mpa, duration from film f " +
                "left join l on f.id = l.film_id " +
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

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> films = new ArrayList<>();
        String sql = "select f.id, f.name, f.description, f.release_date, f.duration, f.mpa " +
                "from film as f " +
                "join likes as l on f.id = l.film_id " +
                "where l.film_id in (select film_id from likes where user_id = ?) " +
                "and l.film_id in (select film_id from likes where user_id = ?) " +
                "group by f.id " +
                "order by count(l.user_id) desc";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        while (rowSet.next()) {
            films.add(mapRowSetToFilm(rowSet));
        }
        return films;
    }

    @Override
    public Collection<Film> getRecommendations(User user) {
        // получаем все фильмы, которым поставили лайки пользователи совпадающие с фильмами, которым поставил лайк user
        String query = """
            select l.film_id, l.user_id, f.id, f.name, f.description, f.release_date, f.mpa, f.duration
            from likes l
            join film f on f.id = l.film_id
            where l.user_id in (
               select fl.user_id from likes fl
               where fl.film_id in (select film_id from likes where likes.user_id = ?)
            )
        """;

        SqlRowSet filmsSet = jdbcTemplate.queryForRowSet(query, user.getId());

        Map<Long, Film> films = new HashMap<>();
        Map<Long, Set<Long>> filmsLikes = new HashMap<>(); // фильм - пользователи
        // подготавливаем данные для алгоритма рекомендаций
        while (filmsSet.next()) {
            if (!films.containsKey(filmsSet.getLong("film_id"))) {
                films.put(filmsSet.getLong("film_id"), mapRowSetToFilm(filmsSet));
            }
            filmsLikes.putIfAbsent(filmsSet.getLong("film_id"), new HashSet<>());
            filmsLikes.get(filmsSet.getLong("film_id")).add(filmsSet.getLong("user_id"));
        }

        // получаем рекомендации для user
        Map<Long, Double> recommendations = new BinarySlopeOne(filmsLikes).predict(user.getId());

        return recommendations.keySet().stream()
                .map(films::get)
                .collect(Collectors.toList());
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
                .genres(genreStorage.getGenresByFilmId(rowSet.getLong("id")))
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
