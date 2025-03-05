package ru.yandex.practicum.filmrate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmrate.model.Review;
import ru.yandex.practicum.filmrate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        log.info("Получен запрос: Ревью по id");
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getAllFilmReviews(@RequestParam int filmId,
                                          @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос: Все ревью к фильму");
        return reviewService.getAllFilmReviews(count, filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("Получен запрос на создание review");
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review newReview) {
        log.info("Получен запрос на изменение review");
        return reviewService.updateReview(newReview);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void likeReview(@PathVariable Long id,
                           @PathVariable Long userId) {
        log.info("Получен запрос на добавления лайка");
        reviewService.likeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dislikeReview(@PathVariable Long id,
                              @PathVariable Long userId) {
        log.info("Получен запрос на добавления дизлайка");
        reviewService.dislikeReview(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable long id) {
        log.info("Получен запрос на удаление review");
        reviewService.deleteReview(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeReview(@PathVariable long id,
                                 @PathVariable long userId) {
        log.info("Получен запрос на удаления лайка");
        reviewService.deleteLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDislikeReview(@PathVariable long id,
                                    @PathVariable long userId) {
        log.info("Получен запрос на удаления дизлайка");
        reviewService.deleteDislikeReview(id, userId);
    }
}
