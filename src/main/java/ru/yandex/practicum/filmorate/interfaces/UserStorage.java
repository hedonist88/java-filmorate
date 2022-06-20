package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    Map<Long, User> getAllUsers();
    User findUserById(long userId);
    User addUser(User user);
    User updateUser(User user);
    void removeUserById(long userId);
}
