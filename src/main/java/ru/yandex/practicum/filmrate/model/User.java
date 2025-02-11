package ru.yandex.practicum.filmrate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmrate.validator.string.NoWhiteSpace;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {

    private Long id;

    @Email(message = "Email не валиднен")
    @NonNull
    private final String email;
    @NotEmpty
    @NoWhiteSpace
    private final String login;

    private String name;

    @Past(message = "Не может быть в будущем")
    private LocalDate birthday;
}
