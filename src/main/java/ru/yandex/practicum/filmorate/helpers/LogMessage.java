package ru.yandex.practicum.filmorate.helpers;

public enum LogMessage {

    USER_UPDATE("Update user with id"),
    USER_ADD("Add user with id"),

    FILM_UPDATE("Update film with id"),
    FILM_ADD("Add user film id");

    private final String message;

    private LogMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
