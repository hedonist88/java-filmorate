package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.FilmStorageImpl;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorageImpl {

    private final Map<Long, Film> films = new HashMap<>();
    protected int lastFilmId = 0;

    public long getLastFilmId(){
        return ++lastFilmId;
    }

    @Override
    public Map<Long, Film> getAllFilms(){
        return new HashMap<>(films);
    }

    @Override
    public Film add(Film film){
        film.setId(getLastFilmId());
        films.put(film.getId(),film);
        return film;
    }

    @Override
    public Film update(Film film){
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
    public Optional<Film> getFilmById(long filmId){
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public void removeFilmById(long filmId) {
        if(films.containsKey(filmId)){
            films.remove(filmId);
        }
    }
}
