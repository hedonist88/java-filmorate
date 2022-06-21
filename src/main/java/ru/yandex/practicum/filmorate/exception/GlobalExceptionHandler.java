package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler
    public ResponseEntity<?> exc(ConstraintViolationException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(final RuntimeException e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of("Not found Error", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public ResponseEntity<Map<String, String>> userAlreadyExist(final RuntimeException e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of("User Already Exist Error", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({FilmAlreadyExistException.class})
    public ResponseEntity<Map<String, String>> filmAlreadyExistException(final RuntimeException e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of("Film Already Exist Error", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidEmailException.class})
    public ResponseEntity<Map<String, String>> handleIncorrectData(final RuntimeException e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of(
                        "Invalid Email Error", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<Map<String, String>> validationException(final RuntimeException e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of(
                        "Validation Error", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidFilmIdException.class})
    public ResponseEntity<Map<String, String>> invalidFilmId(final RuntimeException e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of(
                        "Invalid Film Error", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>>  handleServerError(final Error e) {
        return new ResponseEntity<Map<String, String>>(
                Map.of(
                        "Internal Server Error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler( MethodArgumentNotValidException.class)
    protected ResponseEntity<Map<String, String>>  argumentError(final Error e)
    {
        return new ResponseEntity<Map<String, String>>(
                Map.of(
                        "Internal Server Error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
