package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.FriendStatus;
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
    public Optional<User> getUserById(long userId){
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User add(User user){
        user.setId(getLastUserId());
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User update(User user){
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

    @Override
    public void putFriendsRelation(long userId, long friendId, FriendStatus status) {

    }

    @Override
    public void deleteFriendsRelation(long userId, long friendId) {

    }
}
