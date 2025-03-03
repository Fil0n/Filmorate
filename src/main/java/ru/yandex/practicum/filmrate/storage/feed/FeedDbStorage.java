package ru.yandex.practicum.filmrate.storage.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmrate.model.EventType;
import ru.yandex.practicum.filmrate.model.Feed;
import ru.yandex.practicum.filmrate.model.Operation;
import ru.yandex.practicum.filmrate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class FeedDbStorage implements FeedStorage{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Feed> getFeed(Long userId) {
        List<Feed> feeds = new ArrayList<>();
        String query = "select eventId, userid, eventType, operation, timestamp, entityId from feed where userid in id in (select friend_id from friendship f where f.user_id = ?)";
        SqlRowSet feedSet = jdbcTemplate.queryForRowSet(query, userId);

        while (feedSet.next()) {
            feeds.add(mapRowSet(feedSet));
        }

        return feeds;
    }

    @Override
    public void create(EventType eventType, Operation operation, Long userId, Long entityId) {
        String sql = "insert into Feed set " +
                "user_id = ?,  event_type = ?,  operation = ?, entity_id = ?";

        jdbcTemplate.update(sql, userId, eventType.toString(), operation.toString(), entityId);
    }

    public Feed mapRowSet(SqlRowSet rowSet) {
        return Feed.builder()
                .eventId(rowSet.getLong("eventId"))
                .userId(rowSet.getLong("userId"))
                .eventType(EventType.valueOf(rowSet.getString("eventType")))
                .operation(Operation.valueOf(rowSet.getString("operation")))
                .timestamp(rowSet.getTimestamp("timestamp"))
                .entityId(rowSet.getLong("entityId"))
                .build();
    }

    private Map<String, Object> toMap(Feed feed) {
        Map<String, Object> values = new HashMap<>();
        values.put("userId", feed.getUserId());
        values.put("eventType", feed.getEventType());
        values.put("operation", feed.getOperation());
        values.put("timestamp", feed.getTimestamp());
        values.put("entityId", feed.getEntityId());
        return values;
    }
}
