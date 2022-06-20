package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    protected int lastFilmId = 0;

    public long getLastFilmId(){
        return ++lastFilmId;
    }

    public Film getFilmById(long id){
        return films.get(id);
    }

    @Override
    public Map<Long, Film> getAllFilms(){
        return new HashMap<>(films);
    }

    @Override
    public Film addFilm(Film film){
        film.setId(getLastFilmId());
        films.put(film.getId(),film);
        return film;
    }
    @Override
    public Film updateFilm(Film film){
        films.put(film.getId(),film);
        return film;
    }
    @Override
    public Collection<Film> getTopLikedFilms(int count) {
        return films.entrySet()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getValue().getLikesCount(), f1.getValue().getLikesCount()))
                .limit(count)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
    @Override
    public Film findFilmById(long filmId){
        return Optional.ofNullable(films.get(filmId)).orElseThrow(
                () -> new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage()));
    }

    @Override
    public void removeFilmById(long filmId) {
        if(films.containsKey(filmId)){
            films.remove(filmId);
        }
    }
}
