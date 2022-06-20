package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

public interface UserStorageService {
    void setUserStorage(InMemoryUserStorage userStorage);
}
