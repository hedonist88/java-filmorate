package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    protected int lastUserId = 0;

    @Override
    public Map<Long, User> getAllUsers(){
        return new HashMap<>(users);
    }

    @Override
    public User findUserById(long userId){
        return Optional.ofNullable(users.get(userId)).orElseThrow(
                () -> new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage()));
    }

    @Override
    public User addUser(User user){
        user.setId(getLastUserId());
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User updateUser(User user){
        users.put(user.getId(),user);
        return user;
    }

    public long getLastUserId(){
        return ++lastUserId;
    }

    @Override
    public void removeUserById(long userId) {
        if(users.containsKey(userId)){
            users.remove(userId);
        }
    }
}
