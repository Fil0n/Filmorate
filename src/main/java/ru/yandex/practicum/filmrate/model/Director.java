package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Director {
    private int id;

    @NotBlank
    private final String name;
}
