package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.interfaces.FilmService;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
@Validated
public class GenreController {
    private FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<Collection<Genre>> findAll() {
        return ResponseEntity.ok(filmService.findAllGenres());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Genre> findGenreById(@PathVariable(name = "id") int id)
    {
        return ResponseEntity.ok(filmService.findGenreById(id));
    }
}