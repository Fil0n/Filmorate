package ru.yandex.practicum.filmrate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Review {
    @JsonProperty("reviewId")
    private Long id;

    @JsonProperty("useful")
    private Integer rating;

    @JsonProperty("content")
    @Size(max = 1500, message = "Текст ревью не может содержать более 1500 символов")
    private String textReview;

    @NotNull(message = "ID ревьюера не может быть null")
    private Long userId;

    @NotNull(message = "ID фильма не может быть null")
    private Long filmId;

    @JsonProperty("isPositive")
    @NotNull(message = "Ревью должно быть либо позитивным, либо негативным и не может быть null")
    private Boolean isPositiveReview;
}
