package ru.yandex.practicum.filmrate.storage.review;

import ru.yandex.practicum.filmrate.model.Review;

import java.util.List;

public interface ReviewStorage {
    public Review getReviewById(long id); //+

    public List<Review> getAllFilmReviews(int count, Long filmId); //+

    public Review createReview(Review review);

    public Review updateReview(Review newReview);

    public void likeReview(Long id, Long userId);

    public void dislikeReview(Long id, Long userId);

    public void deleteReview(long id);

    public void deleteLikeReview(long id, long userId);

    public void deleteDislikeReview(long id, long userId);
}
