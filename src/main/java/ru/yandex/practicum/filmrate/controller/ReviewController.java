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
    public Review getById(@PathVariable long id) {
        log.info("Получен запрос: Ревью по id");
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getAllFilmReviews(@RequestParam(required = false) Long filmId,
                                          @RequestParam(defaultValue = "10") int count) {
        log.info("Получен запрос: Все ревью к фильму");
        return reviewService.getAllFilmReviews(count, filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@Valid @RequestBody Review review) {
        log.info("Получен запрос на создание review");
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review newReview) {
        log.info("Получен запрос на изменение review");
        return reviewService.update(newReview);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id,
                     @PathVariable Long userId) {
        log.info("Получен запрос на добавления лайка");
        reviewService.like(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@PathVariable Long id,
                        @PathVariable Long userId) {
        log.info("Получен запрос на добавления дизлайка");
        reviewService.dislike(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("Получен запрос на удаление review");
        reviewService.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id,
                           @PathVariable long userId) {
        log.info("Получен запрос на удаления лайка");
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable long id,
                              @PathVariable long userId) {
        log.info("Получен запрос на удаления дизлайка");
        reviewService.deleteDislike(id, userId);
    }
}
