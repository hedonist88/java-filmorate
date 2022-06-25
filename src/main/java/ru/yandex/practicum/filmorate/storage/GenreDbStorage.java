package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
@Slf4j
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    final String DELETE_QUERY_GENRES = "DELETE FROM a_films_genre " +
            "WHERE film_id = ? AND genre_id NOT IN (?)";
    final String DELETE_QUERY_ALLGENRES = "DELETE FROM a_films_genre " +
            "WHERE film_id = ?";
    final String SELECT_ALL_GENRES = "SELECT * " +
            "FROM a_genre ORDER BY id ASC";
    final String SELECT_GENRE_BY_ID = "SELECT * " +
            "FROM a_genre " +
            "WHERE id = ? LIMIT 1";
    final String SELECT_ALL_RELATIVE_GENRES = "SELECT COUNT(*) AS count " +
            "FROM a_films_genre";
    final String INSERT_QUERY_GENRE =
            "INSERT INTO a_films_genre " +
                    "(film_id, genre_id) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE film_id = ?, genre_id = ?";
    final String SELECT_FILM_GENRES_BY_ID = "SELECT tb2.id, tb2.name " +
            "FROM a_films_genre AS tb1 " +
            "LEFT JOIN a_genre AS tb2 ON tb1.genre_id = tb2.id " +
            "WHERE tb1.film_id = ? " +
            "ORDER BY tb2.id";

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenres(){
        Set<Genre> result = new LinkedHashSet<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_ALL_GENRES);
        while(rows.next()) {
            Genre genre = new Genre();
            genre.setId(rows.getInt("id"));
            genre.setName(rows.getString("name"));
            result.add(genre);
        }
        return result;
    }

    @Override
    public void putFilmGenres(long filmId, Set<Genre> genres){
        genres.forEach(genre -> {
            jdbcTemplate.update(
                    INSERT_QUERY_GENRE,
                    filmId,
                    genre.getId(),
                    filmId,
                    genre.getId()
            );
        });
    };

    @Override
    public void updateFilmGenres(long filmId, Set<Genre> genres){
        if(genres == null) {
            jdbcTemplate.update(
                    DELETE_QUERY_ALLGENRES,
                    filmId
            );
        } else {
            List<String> ids = new ArrayList<>();
            genres.forEach(genre -> {
                ids.add(String.valueOf(genre.getId()));
            });
            jdbcTemplate.update(
                    DELETE_QUERY_GENRES,
                    filmId,
                    ids.toArray()
            );

            putFilmGenres(filmId, genres);
        }
    }

    @Override
    public Set<Genre> getFilmGenresById(long filmId){
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_FILM_GENRES_BY_ID, filmId);
        Set<Genre> result = new LinkedHashSet<>();
        while (rows.next()) {
            Genre genre = new Genre();
            genre.setId(rows.getInt("id"));
            genre.setName(rows.getString("name"));
            result.add(genre);
        }
        //if(result.size() == 0){
        //    return null;
        //} else {
            return result;
        //}
    }
    @Override
    public Optional<Genre> getGenreById(int id){
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_GENRE_BY_ID, id);
        if(rows.next()) {
            Genre genre = new Genre();
            genre.setId(rows.getInt("id"));
            genre.setName(rows.getString("name"));
            log.info(LogMessage.GENRE_FOUND.getMessage() + " {}", rows.getString("id"));
            return Optional.of(genre);
        } else {
            log.info(ErrorMessage.GENRE_NOT_FOUND.getMessage(), id);
            return Optional.empty();
        }
    }

    @Override
    public int countAllRelativeGenres(){
        int count = 0;
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_ALL_RELATIVE_GENRES);
        if(rows.next()) {
            count = rows.getInt("count");
        }
        return count;
    }
}
