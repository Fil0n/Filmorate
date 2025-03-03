package ru.yandex.practicum.filmrate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.model.EventType;
import ru.yandex.practicum.filmrate.model.Feed;
import ru.yandex.practicum.filmrate.model.Operation;
import ru.yandex.practicum.filmrate.storage.feed.FeedStorage;

import java.util.List;

@Service
@Slf4j
public class FeedService {
    @Autowired
    private FeedStorage feedStorage;

    public List<Feed> getFeed(long userId) {
        log.info("Получение ленты пользователя");
        return feedStorage.getFeed(userId);
    }

    public void create(EventType eventType, Operation operationType, long userId, long entityId) {
        log.info("Начато добавление события {} с операцией {} в ленту пользователя {}. Id сущности: {}",
                eventType, operationType, userId, entityId);
        feedStorage.create(eventType, operationType, userId, entityId);;
    }
}
