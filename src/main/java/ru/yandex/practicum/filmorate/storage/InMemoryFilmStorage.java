package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    protected int lastFilmId = 0;

    public Map<Integer, Film> getFilms(){
        return films;
    };

    public int getLastFilmId(){
        return ++lastFilmId;
    }

    public void put(Film film){
        films.put(film.getId(),film);
    }

    public Film getFilmById(int id){
        return films.get(id);
    }
}
