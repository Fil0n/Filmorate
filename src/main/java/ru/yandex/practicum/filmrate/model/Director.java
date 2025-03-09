package ru.yandex.practicum.filmrate.model;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Data
public class Director {
    private int id;
    @NotNull
    private String name;
}
