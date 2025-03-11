package ru.yandex.practicum.filmrate.model;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class Director {
    private int id;
    @NotNull
    private String name;
}
