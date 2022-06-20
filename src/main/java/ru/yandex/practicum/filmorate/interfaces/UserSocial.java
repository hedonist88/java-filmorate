package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

public interface UserSocial {
    Film addLike(int filmId, int userId);
    Film removeLike(int filmId, int userId);
}
