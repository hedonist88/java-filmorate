package ru.yandex.practicum.filmorate.interfaces;

import java.util.Set;

public interface LikeStorage {
    Set<Long> getFilmLikesById(long filmId);
    void putLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
}
