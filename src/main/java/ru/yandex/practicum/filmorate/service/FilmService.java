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
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.UserSocial;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService implements FilmStorage, UserSocial {

    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> getAllFilms() {
        if(filmStorage.getFilms().size() == 0){
            return Collections.emptyList();
        }
        return filmStorage.getFilms().values();
    }

    @Override
    public Film addFilm(Film film) {
        if(film.getId() < 0) {
            throw new InvalidFilmIdException(ErrorMessage.WRONG_FILM_ID.getMessage());
        }
        if(!validateFilm(film)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        if(filmStorage.getFilms().containsKey(film.getId())) {
            throw new FilmAlreadyExistException(ErrorMessage.FILM_IS_ALREADY.getMessage() + " " +
                    film.getId());
        }
        film.setId(filmStorage.getLastFilmId());
        if(!filmStorage.getFilms().containsKey(film.getId())) {
            filmStorage.put(film);
            log.info(LogMessage.FILM_ADD.getMessage() + " {} {}", film.getName(), film.getId());
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if(film.getId() < 0) {
            throw new NotFoundException(ErrorMessage.WRONG_FILM_ID.getMessage());
        }
        if(!validateFilm(film)){
            throw new ValidationException(ErrorMessage.VALIDATE_ERROR.getMessage());
        }
        if(filmStorage.getFilms().containsKey(film.getId())) {
            filmStorage.put(film);
            log.info(LogMessage.FILM_UPDATE.getMessage() + " {} {}", film.getName(), film.getId());
        } else {
            throw new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage());
        }
        return film;
    }

    @Override
    public Collection<Film> getTopLikedFilms(int count){
        System.out.println(filmStorage.getFilms().size());
        if(filmStorage.getFilms().size() == 0){
            return Collections.emptyList();
          //  throw new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage());
        }
        return filmStorage.getFilms().entrySet()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getValue().getLikesCount(), f1.getValue().getLikesCount()))
                .limit(count)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Film findFilmById(int filmId){
        if(!filmStorage.getFilms().containsKey(filmId)){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public Film addLike(int filmId, int userId){
        if(filmStorage.getFilmById(filmId) == null){
            throw new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage());
        }
        if(userStorage.getUserById(userId) == null){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        filmStorage.getFilmById(filmId).getLikeUserIds().add(userId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public Film removeLike(int filmId, int userId){
        if(filmStorage.getFilmById(filmId) == null){
            throw new NotFoundException(ErrorMessage.FILMS_NOT_FOUND.getMessage());
        }
        if(userStorage.getUserById(userId) == null){
            throw new NotFoundException(ErrorMessage.USERS_NOT_FOUND.getMessage());
        }
        if(!filmStorage.getFilmById(filmId).getLikeUserIds().contains(userId)){
            filmStorage.getFilmById(filmId).getLikeUserIds().remove(userId);
        }
        return filmStorage.getFilmById(filmId);
    }

    private boolean validateFilm(Film film){
        boolean result = true;

        if(film.getName() == null || film.getName().isBlank()){
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
