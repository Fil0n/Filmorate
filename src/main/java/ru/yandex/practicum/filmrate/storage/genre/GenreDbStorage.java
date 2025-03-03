package ru.yandex.practicum.filmrate.storage.genre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.exception.ExceptionMessages;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public Collection<Genre> genres;

    @Override
    public Collection<Genre> findAll() {
        if (genres != null) {
            return genres;
        }

        genres = new ArrayList<>();
        SqlRowSet genresSet = jdbcTemplate.queryForRowSet("select id, name from genre");

        while (genresSet.next()) {
            genres.add(mapRowSetToGenre(genresSet));
        }

        return genres.stream().collect(Collectors.toList());
    }

    @Override
    public Genre read(int id) {
        String query = "select id, name from genre where id = ?";
        SqlRowSet genresSet = jdbcTemplate.queryForRowSet(query, id);

        while (genresSet.next()) {
            return mapRowSetToGenre(genresSet);
        }

        throw new NotFoundException(String.format(ExceptionMessages.GENRE_NOT_FOUND_ERROR, id));
    }

    public Set<Genre> getGenresByFilmId(Long id) {
        Set<Genre> genres = new HashSet<>();
        String query = "select id, name from genre where id in (select genre_id from film_genre where film_id = ?)";
        SqlRowSet genresSet = jdbcTemplate.queryForRowSet(query, id);

        while (genresSet.next()) {
            genres.add(mapRowSetToGenre(genresSet));
        }

        return genres;
    }

    private Genre mapRowSetToGenre(SqlRowSet rowSet) {
        return Genre.builder()
                .id(rowSet.getInt("id"))
                .name(rowSet.getString("name"))
                .build();
    }
}
