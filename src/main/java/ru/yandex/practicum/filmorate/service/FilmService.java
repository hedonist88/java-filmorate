package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmService implements FilmServiceImpl {

    private FilmStorageImpl filmStorage;
    private UserStorageImpl userStorage;

    @Autowired
    public FilmService(FilmStorageImpl filmStorage, UserStorageImpl userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> findAllFilms() {
        return filmStorage.getAllFilms().values();
    }

    @Override
    public Film addFilm(Film film) {
        if(!validateFilm(film)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        if(filmStorage.getAllFilms().containsKey(film.getId())) {
            throw new FilmAlreadyExistException(ErrorMessage.FILM_IS_ALREADY.getMessage() + " " +
                    film.getId());
        }
        Film addFilm = filmStorage.add(film);
            log.info(LogMessage.FILM_ADD.getMessage() + " {} {}", film.getName(), film.getId());
        return addFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        if(!validateFilm(film)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        filmStorage.getFilmById(film.getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage()));
        filmStorage.update(film);
        log.info(LogMessage.FILM_UPDATE.getMessage() + " {} {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Collection<Film> findTopLikedFilms(int count){
        if(filmStorage.getAllFilms().size() == 0){
            return Collections.emptyList();
        }
        return filmStorage.getTopLikedFilms(count);
    }

    @Override
    public Film findFilmById(long filmId){
        return filmStorage.getFilmById(filmId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage()));
    }

    @Override
    public Film addLike(long filmId, long userId){
        userStorage.getUserById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage()));;
        Film film = filmStorage.getFilmById(filmId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage()));
        film.getLikeUserIds().add(userId);
        return film;
    }

    @Override
    public Film removeLike(long filmId, long userId){
        userStorage.getUserById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage()));;
        Film film = filmStorage.getFilmById(filmId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage()));
        if(!film.getLikeUserIds().contains(userId)){
            film.getLikeUserIds().remove(userId);
        }
        return film;
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
