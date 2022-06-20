package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.UserSocial;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserStorage {

    private InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User findUserById(int userId){
        if(!userStorage.getUsers().containsKey(userId)){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        return userStorage.getUserById(userId);
    }
    @Override
    public User addUser(User user){
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidEmailException(ErrorMessage.EMPTY_EMAIL.getMessage());
        }
        if(!validateUser(user)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userStorage.getLastUserId());
        if(!userStorage.getUsers().containsKey(user.getId())) {
            userStorage.put(user);
            log.info(LogMessage.USER_ADD.getMessage() + " {} {}", user.getLogin(), user.getId());
        }
        return user;
    }

    @Override
    public User updateUser(User user){
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidEmailException(ErrorMessage.EMPTY_EMAIL.getMessage());
        }
        if(!validateUser(user)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(userStorage.getUsers().containsKey(user.getId())) {
            userStorage.put(user);
            log.info(LogMessage.USER_UPDATE.getMessage() + " {} {}", user.getLogin(), user.getId());
        } else {
            log.info(ErrorMessage.USER_NOT_REGISTER.getMessage() + " {} {}", user.getLogin(), user.getId());
            throw new NotFoundException(ErrorMessage.USER_NOT_REGISTER.getMessage());
        }
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        if(userStorage.getUsers().size() == 0){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        return userStorage.getUsers().values();
    }

    public User addFriend(int userId, int friendId){
        if(!userStorage.getUsers().containsKey(userId) || !userStorage.getUsers().containsKey(friendId)){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        userStorage.getUserById(userId).getFriendsIds().add(friendId);
        userStorage.getUserById(friendId).getFriendsIds().add(userId);
        return userStorage.getUserById(userId);
    }

    public User removeFriend(int userId, int friendId){
        if(!userStorage.getUsers().containsKey(userId) || !userStorage.getUsers().containsKey(friendId)){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        userStorage.getUserById(userId).getFriendsIds().remove(friendId);
        userStorage.getUserById(friendId).getFriendsIds().remove(userId);
        return userStorage.getUserById(userId);
    }

    public Collection<User> findUserFriends(int userId){
        if(!userStorage.getUsers().containsKey(userId) ||
                userStorage.getUserById(userId).getFriendsIds().size() == 0){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        return userStorage.getUserById(userId)
                .getFriendsIds()
                .stream()
                .map(id -> { User u = userStorage.getUserById(id); return u; })
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriendsList(int userId, int otherUserId){
        if(!userStorage.getUsers().containsKey(userId) || !userStorage.getUsers().containsKey(otherUserId)){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        if(userStorage.getUserById(otherUserId).getFriendsIds() == null
                || userStorage.getUserById(otherUserId).getFriendsIds().size() == 0){
            return Collections.emptyList();
        }
        Set<Integer> crossing = new HashSet<>(userStorage.getUserById(otherUserId).getFriendsIds());
        crossing.retainAll(userStorage.getUserById(userId).getFriendsIds());
        return crossing.stream()
                .map(id -> { User u = userStorage.getUserById(id); return u; })
                .collect(Collectors.toList());
    }

    private boolean validateUser(User user){
        boolean result = true;
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if(user.getEmail().isBlank() || !VALID_EMAIL_ADDRESS_REGEX.matcher(user.getEmail()).find()){
            log.info(ErrorMessage.WRONG_EMAIL.getMessage() + " {}", user.getEmail());
            result = false;
        }
        if(user.getLogin().isBlank() || !user.getLogin().matches("^\\S*$")){
            log.info(ErrorMessage.WRONG_LOGIN.getMessage() + " {} {}", user.getLogin(), user.getEmail());
            result = false;
        }
        if(user.getBirthday().isAfter(LocalDate.now())){
            log.info(ErrorMessage.WRONG_BIRTDAY.getMessage() + " {} {}", user.getBirthday(), user.getEmail());
            result = false;
        }
        return result;
    }

}
