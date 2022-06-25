package ru.yandex.practicum.filmorate.helpers;

public enum LogMessage {

    USER_UPDATE("Update user with id"),
    USER_ADD("Add user with id"),
    USER_FOUND("User found with id"),

    MPA_FOUND("MPA Found"),
    GENRE_FOUND("Genre found"),
    FILM_UPDATE("Update film with id"),
    FILM_ADD("Add user film id"),
    FILM_FOUND("Film found with id"),

    LIKE_ADD("Add like filmId userId"),
    LIKE_DELETE("Delete like filmId userId");

    private final String message;

    private LogMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
