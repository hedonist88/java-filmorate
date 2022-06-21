package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Map<Long, Film> getAllFilms();
    Film add(Film film);
    Film update(Film film);
    Collection<Film> getTopLikedFilms(int size);
    Optional<Film> getFilmById(long filmId);
    void removeFilmById(long filmId);
}
