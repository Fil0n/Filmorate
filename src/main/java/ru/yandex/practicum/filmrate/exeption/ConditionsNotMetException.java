package ru.yandex.practicum.filmrate.exeption;

public class ConditionsNotMetException extends Throwable {
    public ConditionsNotMetException(String message) {
        super(message);
    }
}
