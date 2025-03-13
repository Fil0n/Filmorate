package ru.yandex.practicum.filmrate.storage.review;

import ru.yandex.practicum.filmrate.model.Review;

import java.util.List;

public interface ReviewStorage {
    public Review getById(long id);

    public List<Review> getAllFilmReviews(int count, Long filmId);

    public Review create(Review review);

    public Review update(Review newReview);

    public void like(Long id, Long userId);

    public void dislike(Long id, Long userId);

    public void delete(long id);

    public void deleteLike(long id, long userId);

    public void deleteDislike(long id, long userId);
}
