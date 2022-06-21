package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Validated
public class User {
    private long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;
    private LocalDate birthday;
    @JsonIgnore
    private Map<Long, FriendStatus> friendsIds = new HashMap<>();

}
