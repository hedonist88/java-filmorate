package ru.yandex.practicum.filmorate.helpers;

import java.time.LocalDate;

public enum ErrorMessage {

    /* General */
    VALIDATE_ERROR("Validate fields error"),
    USER_NOT_REGISTER("User not registered"),

    FILMS_NOT_FOUND("Films list is empty"),
    USERS_NOT_FOUND("Users list is empty"),

    /* User */
    EMPTY_EMAIL("The email address cannot be empty"),
    WRONG_EMAIL("Wrong email"),
    WRONG_LOGIN("Wrong login"),
    WRONG_BIRTDAY("Wrong date birthday"),

    /* Film */
    WRONG_FILM_ID("Movie ID cannot be negative"),
    FILM_IS_ALREADY("Movie with id already in the database"),
    WRONG_FILM_NAME("Wrong film name"),
    WRONG_FILM_DESCR_LENGTH("Very long description from film"),
    WRONG_FILM_DURATION("Wrong film duration"),
    WRONG_FILM_RELEASE_DATE("Wrong release date");

    private final String message;

    private ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
