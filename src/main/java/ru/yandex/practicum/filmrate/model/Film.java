package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {

    private Long id;

    @NotNull(message = "Не может быть null")
    @NotBlank(message = "Не может быть пустым")
    private final String name;

    @Size(max = 200, message = "Не более 200 символов")
    private String description;

    @FutureOrPresent
    private final LocalDate date;

    @Min(value = 1, message = "Не может быть меньше 1")
    private int duration;
}
