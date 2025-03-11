package ru.yandex.practicum.filmrate.storage.director;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DirectorRowMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("director_name"))
                .build();
    }
}
