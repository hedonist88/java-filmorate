package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.InvalidFilmIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.FilmStorageService;
import ru.yandex.practicum.filmorate.interfaces.UserSocial;
import ru.yandex.practicum.filmorate.interfaces.UserStorageService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService implements UserSocial, FilmStorageService, UserStorageService {

    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;

    @Override
    @Autowired
    public void setFilmStorage(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    @Autowired
    public void setUserStorage(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        if(filmStorage.getAllFilms().size() == 0){
            return Collections.emptyList();
        }
        return filmStorage.getAllFilms().values();
    }

    public Film addFilm(Film film) {
        if(!validateFilm(film)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        if(filmStorage.getAllFilms().containsKey(film.getId())) {
            throw new FilmAlreadyExistException(ErrorMessage.FILM_IS_ALREADY.getMessage() + " " +
                    film.getId());
        }
        Film addfilm = null;
        if(!filmStorage.getAllFilms().containsKey(film.getId())) {
            addfilm = filmStorage.addFilm(film);
            log.info(LogMessage.FILM_ADD.getMessage() + " {} {}", film.getName(), film.getId());
        }
        return addfilm;
    }

    public Film updateFilm(Film film) {
        if(!validateFilm(film)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        filmStorage.findFilmById(film.getId());
        if(filmStorage.getAllFilms().containsKey(film.getId())) {
            filmStorage.updateFilm(film);
            log.info(LogMessage.FILM_UPDATE.getMessage() + " {} {}", film.getName(), film.getId());
        } else {
            throw new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage());
        }
        return film;
    }

    public Collection<Film> getTopLikedFilms(int count){
        if(filmStorage.getAllFilms().size() == 0){
            return Collections.emptyList();
        }
        return filmStorage.getTopLikedFilms(count);
    }

    public Film findFilmById(long filmId){
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public Film addLike(long filmId, long userId){
        userStorage.findUserById(userId);
        filmStorage.getFilmById(filmId).getLikeUserIds().add(userId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public Film removeLike(long filmId, long userId){
        userStorage.findUserById(userId);
        if(!filmStorage.getFilmById(filmId).getLikeUserIds().contains(userId)){
            filmStorage.getFilmById(filmId).getLikeUserIds().remove(userId);
        }
        return filmStorage.getFilmById(filmId);
    }

    private boolean validateFilm(Film film){
        boolean result = true;
        if(film.getName().isBlank()){
            log.info(ErrorMessage.WRONG_FILM_NAME.getMessage() + " {} {}", film.getName(), film.getId());
            result = false;
        }
        if(film.getDescription().length() > 200){
            log.info(ErrorMessage.WRONG_FILM_DESCR_LENGTH.getMessage() + " {}", film.getId());
            result = false;
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))){
            log.info(ErrorMessage.WRONG_FILM_RELEASE_DATE.getMessage() + " {} {}", film.getReleaseDate(), film.getId());
            result = false;
        }
        if(film.getDuration() < 0){
            log.info(ErrorMessage.WRONG_FILM_DURATION.getMessage() + " {} {}", film.getDuration(), film.getId());
            result = false;
        }
        return result;
    }
}
