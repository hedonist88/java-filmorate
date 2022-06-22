package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.UserService;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserStorage userStorage;
    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User findUserById(long userId){
        return userStorage.getUserById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage()));
    }

    @Override
    public User addUser(User user){
        if(!validateUser(user)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        User addUser = userStorage.add(user);
        log.info(LogMessage.USER_ADD.getMessage() + " {} {}", user.getLogin(), user.getId());
        return addUser;
    }

    @Override
    public User updateUser(User user){
        if(!validateUser(user)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        findUserById(user.getId());
        if(userStorage.getAllUsers().containsKey(user.getId())) {
            userStorage.update(user);
            log.info(LogMessage.USER_UPDATE.getMessage() + " {} {}", user.getLogin(), user.getId());
        } else {
            log.info(ErrorMessage.USER_NOT_REGISTER.getMessage() + " {} {}", user.getLogin(), user.getId());
            throw new NotFoundException(ErrorMessage.USER_NOT_REGISTER.getMessage());
        }
        return user;
    }

    @Override
    public Collection<User> findAllUsers() {
        return userStorage.getAllUsers().values();
    }

    @Override
    public User addFriend(long userId, long friendId){
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.getFriendsIds().put(friendId, FriendStatus.CONFIRMED);
        friend.getFriendsIds().put(userId, FriendStatus.CONFIRMED);
        return user;
    }

    @Override
    public User removeFriend(long userId, long friendId){
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.getFriendsIds().remove(friendId);
        friend.getFriendsIds().remove(userId);
        return user;
    }

    @Override
    public Collection<User> findUserFriends(long userId){
        User user = findUserById(userId);
        if(user.getFriendsIds().size() == 0){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        return user.getFriendsIds().keySet()
                .stream()
                .map(id -> { User u = findUserById(id);
                        return u; })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> findCommonFriendsList(long userId, long otherUserId){
        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);
        if(otherUser.getFriendsIds().size() == 0){
            return Collections.emptyList();
        }
        Set<Long> crossing = new HashSet<>(otherUser.getFriendsIds().keySet());
        crossing.retainAll(user.getFriendsIds().keySet());
        return crossing.stream()
                .map(id -> { User u = findUserById(id);
                    return u; })
                .collect(Collectors.toList());
    }

    private boolean validateUser(User user){
        boolean result = true;
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
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(user.getBirthday().isAfter(LocalDate.now())){
            log.info(ErrorMessage.WRONG_BIRTDAY.getMessage() + " {} {}", user.getBirthday(), user.getEmail());
            result = false;
        }
        return result;
    }

}
