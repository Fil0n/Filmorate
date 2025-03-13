package ru.yandex.practicum.filmrate.storage.feed;

import ru.yandex.practicum.filmrate.model.EventType;
import ru.yandex.practicum.filmrate.model.Feed;
import ru.yandex.practicum.filmrate.model.Operation;

import java.util.List;

public interface FeedStorage {
    List<Feed> getFeed(Long userId);

    void create(EventType eventType, Operation operation, Long userId, Long entityId);
}
