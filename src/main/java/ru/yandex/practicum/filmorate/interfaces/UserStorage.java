package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    Map<Long, User> getAllUsers();
    Optional<User> getUserById(long userId);
    User add(User user);
    User update(User user);
    void removeUserById(long userId);
}
