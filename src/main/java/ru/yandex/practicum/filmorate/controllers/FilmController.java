package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmIdException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<Film>> findAll() {
        return films != null
                ? new ResponseEntity<>(films.values(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Film> create(@RequestBody Film film) {
        if(film == null) {
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
        if(film.getId() < 0) {
            throw new InvalidFilmIdException("Movie ID cannot be negative.");
        }
        if(!validateFilm(film)){
            throw new ValidationException("Validate film fields error");
        }
        if(films.containsKey(film.getId())) {
            throw new FilmAlreadyExistException("Movie with specified id " +
                    film.getId() + " already in the database.");
        }
        if(films.size() == 0){
            film.setId(1);
        } else {
            film.setId(films.size() + 1);
        }
        if(!films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Add film {} with id {}", film.getName(), film.getId());
        }
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Film> put(@RequestBody Film film) {
        if(film == null) {
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
        if(film.getId() < 0) {
            throw new InvalidFilmIdException("Movie ID cannot be negative.");
        }
        if(!validateFilm(film)){
            throw new ValidationException("Validate film fields error");
        }
        if(films.containsKey(film.getId())) {
            film.setId(films.get(film.getId()).getId());
            films.put(film.getId(), film);
            log.info("Update film {} with id {}", film.getName(), film.getId());
        }
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    private boolean validateFilm(Film film){
        boolean result = true;

        if(film.getName() == null || film.getName().isBlank()){
            log.info("Wrong name {} from film {}", film.getName(), film.getId());
            result = false;
        }
        if(film.getDescription().length() > 200){
            log.info("Very long description from film {}", film.getId());
            result = false;
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))){
            log.info("Wrong release date {} from film {}", film.getReleaseDate(), film.getId());
            result = false;
        }
        if(film.getDuration() < 0){
            log.info("Wrong duration {} from film {}", film.getDuration(), film.getId());
            result = false;
        }
        return result;
    }
}
