package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.UserStorageService;
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
public class UserService implements UserStorageService {

    private InMemoryUserStorage userStorage;

    @Override
    @Autowired
    public void setUserStorage(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User findUserById(long userId){
        return userStorage.findUserById(userId);
    }

    public User addUser(User user){
        if(!validateUser(user)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        User adduser = null;
        if(!userStorage.getAllUsers().containsKey(user.getId())) {
            adduser = userStorage.addUser(user);
            log.info(LogMessage.USER_ADD.getMessage() + " {} {}", user.getLogin(), user.getId());
        }
        return adduser;
    }

    public User updateUser(User user){
        if(!validateUser(user)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(userStorage.getAllUsers().containsKey(user.getId())) {
            userStorage.updateUser(user);
            log.info(LogMessage.USER_UPDATE.getMessage() + " {} {}", user.getLogin(), user.getId());
        } else {
            log.info(ErrorMessage.USER_NOT_REGISTER.getMessage() + " {} {}", user.getLogin(), user.getId());
            throw new NotFoundException(ErrorMessage.USER_NOT_REGISTER.getMessage());
        }
        return user;
    }

    public Collection<User> getAllUsers() {
        if(userStorage.getAllUsers().size() == 0){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        return userStorage.getAllUsers().values();
    }

    public User addFriend(long userId, long friendId){
        userStorage.findUserById(userId).getFriendsIds().add(friendId);
        userStorage.findUserById(friendId).getFriendsIds().add(userId);
        return userStorage.findUserById(userId);
    }

    public User removeFriend(long userId, long friendId){
        userStorage.findUserById(userId).getFriendsIds().remove(friendId);
        userStorage.findUserById(friendId).getFriendsIds().remove(userId);
        return userStorage.findUserById(userId);
    }

    public Collection<User> findUserFriends(long userId){
        if(userStorage.findUserById(userId).getFriendsIds().size() == 0){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        return userStorage.findUserById(userId)
                .getFriendsIds()
                .stream()
                .map(id -> { User u = userStorage.findUserById(id); return u; })
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriendsList(long userId, long otherUserId){
        if(!userStorage.getAllUsers().containsKey(otherUserId)){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        if(userStorage.findUserById(otherUserId).getFriendsIds() == null
                || userStorage.findUserById(otherUserId).getFriendsIds().size() == 0){
            return Collections.emptyList();
        }
        Set<Long> crossing = new HashSet<>(userStorage.findUserById(otherUserId).getFriendsIds());
        crossing.retainAll(userStorage.findUserById(userId).getFriendsIds());
        return crossing.stream()
                .map(id -> { User u = userStorage.findUserById(id); return u; })
                .collect(Collectors.toList());
    }

    private boolean validateUser(User user){
        boolean result = true;
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidEmailException(ErrorMessage.EMPTY_EMAIL.getMessage());
        }
        if(!VALID_EMAIL_ADDRESS_REGEX.matcher(user.getEmail()).find()){
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
