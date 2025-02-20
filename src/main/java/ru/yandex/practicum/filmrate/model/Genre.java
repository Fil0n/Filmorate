package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Genre {
    @NotNull
    private Integer id;
    @NotNull
    private String name;
}
