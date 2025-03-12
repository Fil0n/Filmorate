package ru.yandex.practicum.filmrate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.exception.ExceptionMessages;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.EventType;
import ru.yandex.practicum.filmrate.model.Feed;
import ru.yandex.practicum.filmrate.model.Operation;
import ru.yandex.practicum.filmrate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmrate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FeedService {
    @Autowired
    private FeedStorage feedStorage;
    @Autowired
    private UserStorage userStorage;

    public List<Feed> getFeed(long userId) {
        log.info("Получение ленты пользователя");
        Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR,
                        userId)));
        return feedStorage.getFeed(userId);
    }

    public void create(EventType eventType, Operation operationType, long userId, long entityId) {
        log.info("Начато добавление события {} с операцией {} в ленту пользователя {}. Id сущности: {}",
                eventType, operationType, userId, entityId);
        Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR,
                        userId)));
        feedStorage.create(eventType, operationType, userId, entityId);
    }
}
