package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private FilmStorage filmStorage;

    private UserStorage userStorage;

    @Autowired
    public FilmServiceImpl(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage")UserStorage userStorage) {
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
        if(film.getId() > 0 && filmStorage.getAllFilms().containsKey(film.getId())) {
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
        findFilmById(film.getId());
        Film updFilm = filmStorage.update(film);
        log.info(LogMessage.FILM_UPDATE.getMessage() + " {} {}", film.getName(), film.getId());
        return updFilm;
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
                () -> new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage()));
        Film film = findFilmById(filmId);
        if(!film.getLikeUserIds().contains(userId)) {
            filmStorage.putLike(filmId, userId);
        }
        return film;
    }

    @Override
    public Film removeLike(long filmId, long userId){
        userStorage.getUserById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage()));
        Film film = findFilmById(filmId);
        if(film.getLikeUserIds().contains(userId)){
            filmStorage.deleteLike(filmId, userId);
        }
        return film;
    }

    public Collection<Mpa> findAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Mpa findMpaById(int id){
        return filmStorage.getMpaById(id).orElseThrow(
                () -> new NotFoundException(ErrorMessage.MPA_NOT_FOUND.getMessage()));
    }

    @Override
    public Collection<Genre> findAllGenres() {
        return filmStorage.getAllGenres();
    }

    @Override
    public Genre findGenreById(int id) {
        return filmStorage.getGenreById(id).orElseThrow(
                () -> new NotFoundException(ErrorMessage.GENRE_NOT_FOUND.getMessage()));
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
        if(film.getMpa() == null){
            log.info(ErrorMessage.WRONG_FILM_MPA.getMessage() + " {}", film.getId());
            result = false;
        }
        return result;
    }
}
