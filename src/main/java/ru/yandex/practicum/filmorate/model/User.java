package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    //@JsonIgnore
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
