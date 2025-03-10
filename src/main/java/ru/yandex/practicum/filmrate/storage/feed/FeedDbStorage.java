package ru.yandex.practicum.filmrate.storage.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.model.EventType;
import ru.yandex.practicum.filmrate.model.Feed;
import ru.yandex.practicum.filmrate.model.Operation;
import java.util.ArrayList;
import java.util.List;


@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getFeed(Long userId) {
        List<Feed> feeds = new ArrayList<>();
        String query = "select id, user_id, event_type, operation, timestamp, entity_id from feed where user_id = ?";
        SqlRowSet feedSet = jdbcTemplate.queryForRowSet(query, userId);

        while (feedSet.next()) {
            feeds.add(mapRowSet(feedSet));
        }

        return feeds;
    }

    @Override
    public void create(EventType eventType, Operation operation, Long userId, Long entityId) {
        String sql = "insert into Feed set " +
                "user_id = ?, event_type = ?, operation = ?, entity_id = ?";

        jdbcTemplate.update(sql, userId, eventType.toString(), operation.toString(), entityId);
    }

    public Feed mapRowSet(SqlRowSet rowSet) {
        return Feed.builder()
                .eventId(rowSet.getLong("id"))
                .userId(rowSet.getLong("user_id"))
                .eventType(EventType.valueOf(rowSet.getString("event_type")))
                .operation(Operation.valueOf(rowSet.getString("operation")))
                .timestamp(rowSet.getTimestamp("timestamp").getTime())
                .entityId(rowSet.getLong("entity_id"))
                .build();
    }
}
