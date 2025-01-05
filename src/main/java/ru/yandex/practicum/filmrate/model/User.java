package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class User {
    private Long id;
    @Email (message = "Email не валиднен")
    @NonNull
    private final String email;

    @NonNull
    private final String login;
    private String name;

    @PastOrPresent(message = "Не может быть в будущем")
    private LocalDate birthday;
}
