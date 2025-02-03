package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmrate.validator.String.NoWhiteSpace;

import java.time.LocalDate;

@Data
public class User {

    private Long id;

    @Email(message = "Email не валиднен")
    @NonNull
    private final String email;

    @NoWhiteSpace
    private final String login;

    private String name;

    @PastOrPresent(message = "Не может быть в будущем")
    private LocalDate birthday;
}
