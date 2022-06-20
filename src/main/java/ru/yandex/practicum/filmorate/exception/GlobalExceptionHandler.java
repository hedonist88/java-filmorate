package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{
    @ExceptionHandler(value = {NotFoundException.class, UserAlreadyExistException.class,
                                FilmAlreadyExistException.class})
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleNotFound(final RuntimeException e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of(
                        "Not found Error", e.getMessage(),
                        "User Already Exist Error", e.getMessage(),
                        "Film Already Exist Error", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidEmailException.class, ValidationException.class,
                            InvalidFilmIdException.class})
    public ResponseEntity<Map<String, String>> handleIncorrectData(final RuntimeException e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of(
                        "Invalid Email Error", e.getMessage(),
                        "Validation Error", e.getMessage(),
                        "Invalid Film Error", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>>  handleServerError(final Error e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of(
                        "Internal Server Error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
