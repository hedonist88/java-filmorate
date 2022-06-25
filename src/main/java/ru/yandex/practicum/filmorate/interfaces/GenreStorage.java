package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> getAllGenres();
    void putFilmGenres(long filmId, Set<Genre> genres);
    void updateFilmGenres(long filmId, Set<Genre> genres);
    Set<Genre> getFilmGenresById(long filmId);
    public Optional<Genre> getGenreById(int id);
    int countAllRelativeGenres();
}
