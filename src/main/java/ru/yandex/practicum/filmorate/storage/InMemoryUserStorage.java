package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    protected int lastUserId = 0;

    public Map<Integer, User> getUsers(){
        return users;
    };

    public int getLastUserId(){
        return ++lastUserId;
    }

    public void put(User user){
        users.put(user.getId(),user);
    }

    public User getUserById(int id){
        return users.get(id);
    }

}
