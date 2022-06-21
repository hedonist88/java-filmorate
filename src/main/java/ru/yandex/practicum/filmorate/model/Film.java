package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Validated
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    @JsonIgnore
    private Set<Long> likeUserIds = new HashSet<>();
    @JsonIgnore
    private Genre genre;
    @JsonIgnore
    private Mpa mpa;

    @JsonIgnore
    public int getLikesCount(){
        int count = 0;
        if(likeUserIds != null){
            count = likeUserIds.size();
        }
        return count;
    }

}
