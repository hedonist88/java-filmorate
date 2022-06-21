package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.interfaces.FilmService;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> findAll() {
        return ResponseEntity.ok(filmService.findAllFilms());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Film> findFilmById(@PathVariable(name = "id") long filmId)
    {
        if(filmId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(filmService.findFilmById(filmId));
    }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) {
        return ResponseEntity.ok(filmService.addFilm(film));
    }

    @PutMapping
    public ResponseEntity<Film> put(@RequestBody Film film) {
        return ResponseEntity.ok(filmService.updateFilm(film));
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<Film> putLike(
            @PathVariable(name = "id") int filmId, @PathVariable(name = "userId") long userId) {
        if(filmId < 0 || userId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(filmService.addLike(filmId, userId));
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public ResponseEntity<Film> removeLike(
            @PathVariable(name = "id") long filmId, @PathVariable(name = "userId") long userId) {
        if(filmId < 0 || userId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
            return ResponseEntity.ok(filmService.removeLike(filmId, userId));
    }

    @GetMapping(value = "/popular")
    public ResponseEntity<Collection<Film>> findPopularFilms(
            @Positive
            @RequestParam(name="count", defaultValue = "10") int count){
        return ResponseEntity.ok(filmService.findTopLikedFilms(count));
    }

}
