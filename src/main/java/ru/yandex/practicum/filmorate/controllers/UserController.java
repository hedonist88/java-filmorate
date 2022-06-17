package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    @ResponseBody
    public ResponseEntity<Collection<User>> findAll() {
        return users != null
                ? new ResponseEntity<>(users.values(), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<User> create(@RequestBody User user) {
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidEmailException("The email address cannot be empty.");
        }
        if(!validateUser(user)){
            throw new ValidationException("Validate user fields error");
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(users.size() == 0){
            user.setId(1);
        } else {
            user.setId(users.size() + 1);
        }
        if(!users.containsKey(user.getId())) {
            //throw new UserAlreadyExistException("User with email " +
            //        user.getEmail() + " already registered.");
            users.put(user.getId(), user);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<User> put(@RequestBody User user) {
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidEmailException("The email address cannot be empty.");
        }
        if(!validateUser(user)){
            throw new ValidationException("Validate user fields error");
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(users.containsKey(user.getId())) {
            user.setId(users.get(user.getId()).getId());
            users.put(user.getId(), user);
        } else {
            throw new UserAlreadyExistException("User with email " +
                    user.getEmail() + " not registered.");
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private boolean validateUser(User user){
        boolean result = true;
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        if(user.getEmail().isBlank() || !VALID_EMAIL_ADDRESS_REGEX.matcher(user.getEmail()).find()){
            log.info("Wrong email {}", user.getEmail());
            result = false;
        }
        if(user.getLogin().isBlank() || user.getLogin().matches("\\s+")){
            log.info("Wrong login {} from user {}", user.getLogin(), user.getEmail());
            result = false;
        }
        if(user.getBirthday().isAfter(LocalDate.now())){
            log.info("Wrong date birthday {} from user {}", user.getBirthday(), user.getEmail());
            result = false;
        }
        return result;
    }
}
