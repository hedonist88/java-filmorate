package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

public interface UserSocial {
    Film addLike(long filmId, long userId);
    Film removeLike(long filmId, long userId);
}
