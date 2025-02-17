package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmrate.validator.date.MinDate;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
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

    private Integer rating;

    private List<Integer> genres;
}
