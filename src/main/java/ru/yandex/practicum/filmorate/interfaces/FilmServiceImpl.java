package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

public interface FilmServiceImpl {
    Collection<Film> findAllFilms();
    Film findFilmById(long filmId);
    Film addFilm(Film film);
    Film updateFilm(Film film);
    Film addLike(long filmId, long userId);
    Film removeLike(long filmId, long userId);
    Collection<Film> findTopLikedFilms(int count);

}
