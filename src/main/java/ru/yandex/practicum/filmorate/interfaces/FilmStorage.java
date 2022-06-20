package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getAllFilms();
    Film addFilm(Film film);
    Film updateFilm(Film film);
    Collection<Film> getTopLikedFilms(int size);
    Film findFilmById(long filmId);
    void removeFilmById(long filmId);
}
