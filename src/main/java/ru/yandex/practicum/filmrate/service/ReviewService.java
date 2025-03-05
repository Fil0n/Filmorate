package ru.yandex.practicum.filmrate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmrate.model.Review;
import ru.yandex.practicum.filmrate.storage.review.ReviewStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    private final ReviewStorage reviewStorage;

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id);
    }

    public List<Review> getAllFilmReviews(int count, long filmId) {
        return reviewStorage.getAllFilmReviews(count, filmId);
    }

    public Review createReview(Review review) {
        return reviewStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        if (review.getId() == null) {
            throw new ValidationException("Вы не указали id ревью, которое хотите изменить");
        }

        return reviewStorage.updateReview(review);
    }

    public void likeReview(Long id, Long userId) {
        reviewStorage.likeReview(id, userId);
    }

    public void dislikeReview(Long id, Long userId) {
        reviewStorage.dislikeReview(id, userId);
    }

    public void deleteReview(long id) {
        reviewStorage.deleteReview(id);
    }

    public void deleteLikeReview(long id, long userId) {
        reviewStorage.deleteLikeReview(id, userId);
    }

    public void deleteDislikeReview(long id, long userId) {
        reviewStorage.deleteDislikeReview(id, userId);
    }
}
