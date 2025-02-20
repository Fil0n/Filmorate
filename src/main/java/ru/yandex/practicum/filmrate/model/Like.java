package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Like {
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
}
