package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.*;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<User>> findAll() {
        return userService.getAllUsers() != null
                ? new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<User> findUserById(@PathVariable(name = "id") int userId)
    {
        if(userId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userService.findUserById(userId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<User> create(@RequestBody User user) {
        if(user == null) {
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userService.addUser(user), HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<User> put(@RequestBody User user) {
        if(user == null) {
            return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public ResponseEntity<User> putUserFriend(
            @PathVariable(name = "id") int userId ,@PathVariable(name = "friendId") int friendId) {
        if(userId < 0 || friendId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(userService.addFriend(userId, friendId), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteUserFriend(
            @PathVariable(name = "id") int userId ,@PathVariable(name = "friendId") int friendId) {
        if(userId < 0 || friendId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(userService.removeFriend(userId, friendId), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/friends")
    public ResponseEntity<Collection<User>> getUserFriends( @PathVariable(name = "id") int userId){
        if(userId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<User>>(userService.findUserFriends(userId), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriendsList(
            @PathVariable(name = "id") int userId, @PathVariable(name = "otherId") int otherUserId){
        if(userId < 0 || otherUserId < 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Collection<User>>(
                userService.findCommonFriendsList(userId, otherUserId), HttpStatus.OK);
    }

}
