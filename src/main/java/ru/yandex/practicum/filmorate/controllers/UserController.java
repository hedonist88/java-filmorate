package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.interfaces.UserServiceImpl;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Collection<User>> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findUserById(@PathVariable(name = "id") long userId)
    {
        return ResponseEntity.ok(userService.findUserById(userId));
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.ok(userService.addUser(user));
    }

    @PutMapping
    public ResponseEntity<User> put(@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public ResponseEntity<User> putUserFriend(
            @PathVariable(name = "id") long userId ,@PathVariable(name = "friendId") long friendId) {
        if(userId < 0 || friendId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.addFriend(userId, friendId));
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public ResponseEntity<User> deleteUserFriend(
            @PathVariable(name = "id") long userId ,@PathVariable(name = "friendId") long friendId) {
        if(userId < 0 || friendId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.removeFriend(userId, friendId));
    }

    @GetMapping(value = "/{id}/friends")
    public ResponseEntity<Collection<User>> getUserFriends( @PathVariable(name = "id") long userId){
        if(userId < 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.findUserFriends(userId));
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public ResponseEntity<Collection<User>> getCommonFriendsList(
            @PathVariable(name = "id") int userId, @PathVariable(name = "otherId") int otherUserId){
        if(userId < 0 || otherUserId < 0) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(userService.findCommonFriendsList(userId, otherUserId));
    }

}
