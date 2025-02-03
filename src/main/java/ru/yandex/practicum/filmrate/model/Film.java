package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmrate.validator.Date.MinDate;

import java.time.LocalDate;

@Data
public class Film {

    private Long id;

    @NotEmpty(message = "Название не может быть пустым")
    private final String name;

    @NotEmpty(message = "Описание не может быть пустым")
    @Size(max = 200, message = "Описание должно содержать более 200 символов")
    private String description;

    @MinDate
    private final LocalDate releaseDate;

    @Min(value = 1, message = "Не может быть меньше 1")
    private int duration;
}
