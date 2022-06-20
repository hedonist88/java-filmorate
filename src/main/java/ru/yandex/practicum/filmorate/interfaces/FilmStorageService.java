package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

public interface FilmStorageService {
    void setFilmStorage(InMemoryFilmStorage filmStorage);
}
