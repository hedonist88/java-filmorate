package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;

public interface MpaStorage {
    Collection<Mpa> getAllMpa();
    Optional<Mpa> getMpaById(int id);
}
