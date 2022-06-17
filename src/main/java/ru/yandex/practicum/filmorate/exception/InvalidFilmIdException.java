package ru.yandex.practicum.filmorate.exception;

public class InvalidFilmIdException extends RuntimeException {
    public InvalidFilmIdException(String s) {
        super(s);
    }
}