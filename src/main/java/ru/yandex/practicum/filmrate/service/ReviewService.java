package ru.yandex.practicum.filmrate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.exception.ExceptionMessages;
import ru.yandex.practicum.filmrate.exception.NotFoundException;
import ru.yandex.practicum.filmrate.model.EventType;
import ru.yandex.practicum.filmrate.model.Operation;
import ru.yandex.practicum.filmrate.model.Review;
import ru.yandex.practicum.filmrate.storage.film.FilmStorage;
import ru.yandex.practicum.filmrate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmrate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedService feedService;


    public Review getById(long id) {
        try {
            return reviewStorage.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Ревью с таким id: " + id + " не существует.");
        }
    }

    public List<Review> getAllFilmReviews(int count, Long filmId) {
        if (filmId != null) {
            Optional.ofNullable(filmStorage.read(filmId))
                    .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR,
                            filmId)));
        }

        return reviewStorage.getAllFilmReviews(count, filmId);
    }

    public Review create(Review review) {
        Optional.ofNullable(filmStorage.read(review.getFilmId()))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR,
                        review.getFilmId())));
        Optional.ofNullable(userStorage.read(review.getUserId()))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR,
                        review.getUserId())));

        Review createdReview =  reviewStorage.create(review);
        feedService.create(EventType.REVIEW, Operation.ADD, review.getUserId(), review.getId());
        return createdReview;
    }

    public Review update(Review newReview) {
        if (newReview.getId() == null) {
            throw new ValidationException("Вы не указали id ревью, которое хотите изменить");
        }

        Optional.ofNullable(filmStorage.read(newReview.getFilmId()))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.FILM_NOT_FOUND_ERROR,
                        newReview.getFilmId())));
        Optional.ofNullable(userStorage.read(newReview.getUserId()))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR,
                        newReview.getUserId())));
        Optional.ofNullable(reviewStorage.getById(newReview.getId()))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR,
                        newReview.getId())));
            feedService.create(EventType.REVIEW, Operation.UPDATE, newReview.getUserId(), newReview.getId());
            return reviewStorage.update(newReview);

    }

    public void like(Long id, Long userId) {
        Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        Optional.ofNullable(reviewStorage.getById(id))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR, id)));

        reviewStorage.like(id, userId);
    }

    public void dislike(Long id, Long userId) {
        Optional.ofNullable(userStorage.read(userId))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND_ERROR, userId)));
        Optional.ofNullable(reviewStorage.getById(id))
                .orElseThrow(() -> new NotFoundException(String.format(ExceptionMessages.REVIEW_NOT_FOUND_ERROR, id)));

        reviewStorage.dislike(id, userId);
    }

    public void delete(long id) {
        Review review = getById(id);
        reviewStorage.delete(id);
        feedService.create(EventType.REVIEW, Operation.REMOVE, review.getUserId(), review.getId());
    }

    public void deleteLike(long id, long userId) {
        reviewStorage.deleteLike(id, userId);

    }

    public void deleteDislike(long id, long userId) {
        reviewStorage.deleteDislike(id, userId);

    }
}
