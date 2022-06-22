package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    @Override
    public Map<Long, Film> getAllFilms() {
        return null;
    }

    @Override
    public Film add(Film film) {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public Collection<Film> getTopLikedFilms(int size) {
        return null;
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        return Optional.empty();
    }

    @Override
    public void removeFilmById(long filmId) {

    }
}
