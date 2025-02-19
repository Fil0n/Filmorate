package ru.yandex.practicum.filmrate.storage.MPA;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.exception.ExceptionMessages;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.MPA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class MPADBStorage implements MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<MPA> findAll() {
        List<MPA> mpas = new ArrayList<>();
        SqlRowSet ratingsSet = jdbcTemplate.queryForRowSet("select * from mpa");

        while (ratingsSet.next()) {
            mpas.add(mapRowSetToRating(ratingsSet));
        }

        return mpas.stream().collect(Collectors.toList());
    }

    @Override
    public MPA read(int id) {
        String query = "select * from mpa where id = ?";
        SqlRowSet ratingSet = jdbcTemplate.queryForRowSet(query, id);

        while (ratingSet.next()) {
            return mapRowSetToRating(ratingSet);
        }

        throw new NotFoundException(String.format(ExceptionMessages.MPA_NOT_FOUND_ERROR, id));
    }

    private MPA mapRowSetToRating(SqlRowSet rowSet) {
        return MPA.builder()
                .id(rowSet.getInt("id"))
                .name(rowSet.getString("name"))
                .build();
    }
}
