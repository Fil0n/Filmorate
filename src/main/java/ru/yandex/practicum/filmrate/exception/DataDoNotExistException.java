package ru.yandex.practicum.filmrate.exception;

public class DataDoNotExistException extends RuntimeException {
    public DataDoNotExistException(String message) {
        super(message);
    }
}
