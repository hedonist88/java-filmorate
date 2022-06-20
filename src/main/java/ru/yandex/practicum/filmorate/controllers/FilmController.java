package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<Film>> findAll() {
        return filmService.getAllFilms() != null
                ? new ResponseEntity<>(filmService.getAllFilms(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<Film> findFilmById(@PathVariable(name = "id") int filmId)
    {
        if(filmId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(filmService.findFilmById(filmId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Film> create(@RequestBody Film film) {
        if(film == null) {
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(filmService.addFilm(film), HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Film> put(@RequestBody Film film) {
        if(film == null) {
            return new ResponseEntity<>(film, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(filmService.updateFilm(film), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<Film> putLike(
            @PathVariable(name = "id") int filmId, @PathVariable(name = "userId") int userId) {
        if(filmId < 0 || userId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Film>(filmService.addLike(filmId, userId), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(
            @PathVariable(name = "id") int filmId, @PathVariable(name = "userId") int userId) {
        if(filmId < 0 || userId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Film>(filmService.removeLike(filmId, userId), HttpStatus.OK);
    }

    @GetMapping(value = "/popular")
    public ResponseEntity<Collection<Film>> getPopularFilms(
            @RequestParam(name="count", defaultValue = "10") Integer count){
        if(count < 1) count = 10;
        return new ResponseEntity<Collection<Film>>(filmService.getTopLikedFilms(count), HttpStatus.OK);
    }

}
