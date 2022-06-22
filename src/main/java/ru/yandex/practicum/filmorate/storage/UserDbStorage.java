package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    @Override
    public Map<Long, User> getAllUsers() {
        return null;
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.empty();
    }

    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void removeUserById(long userId) {

    }
}
