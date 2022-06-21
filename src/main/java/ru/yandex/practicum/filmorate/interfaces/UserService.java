package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.Collection;

public interface UserService {
    Collection<User>  findAllUsers();
    User findUserById(long userId);
    User addUser(User user);
    User updateUser(User user);
    User addFriend(long userId, long friendId);
    User removeFriend(long userId, long friendId);
    Collection<User> findUserFriends(long userId);
    Collection<User> findCommonFriendsList(long userId, long otherUserId);
}
