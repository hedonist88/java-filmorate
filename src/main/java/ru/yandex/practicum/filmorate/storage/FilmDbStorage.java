package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    final String INSERT_QUERY = "INSERT INTO a_films " +
            "(name, descr, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

    final String INSERT_QUERY_FILM_LIKE = "INSERT INTO a_likes " +
            "(user_id, film_id) VALUES (?, ?)";

    final String INSERT_QUERY_GENRE =
            "INSERT INTO a_films_genre " +
                    "(film_id, genre_id) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE film_id = ?, genre_id = ?";
    final String UPDATE_QUERY = "UPDATE a_films SET name = ?, descr = ?," +
            "release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    final String SELECT_ALL = "SELECT tb1.*, tb2.id AS mpaid, tb2.name AS mpaname " +
            "FROM a_films AS tb1 " +
            "LEFT JOIN a_mpa AS tb2 ON tb2.id = tb1.mpa_id";
    final String SELECT_ALL_MPA = "SELECT * " +
            "FROM a_mpa ORDER BY id ASC ";

    final String SELECT_ALL_GENRES = "SELECT * " +
            "FROM a_genre ORDER BY id ASC";
    final String SELECT_ALL_RELATIVE_GENRES = "SELECT COUNT(*) AS count " +
            "FROM a_films_genre";

    final String SELECT_FILM_BY_ID ="SELECT tb1.*, tb2.id AS mpaid, tb2.name AS mpaname " +
            "FROM a_films AS tb1 " +
            "LEFT JOIN a_mpa AS tb2 ON tb2.id = tb1.mpa_id " +
            "WHERE tb1.id = ? ";

    final String DELETE_QUERY_FILM_LIKE = "DELETE FROM a_likes WHERE user_id = ? AND film_id = ?";

    final String DELETE_QUERY = "DELETE FROM a_films WHERE id = ?";

    final String DELETE_QUERY_GENRES = "DELETE FROM a_films_genre " +
            "WHERE film_id = ? AND genre_id NOT IN (?)";
    final String DELETE_QUERY_ALLGENRES = "DELETE FROM a_films_genre " +
            "WHERE film_id = ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, Film> getAllFilms() {
        Map<Long, Film> result = new HashMap<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_ALL);
        while(rows.next()) {
            Film film = buildFilmFromRow(rows);
            result.put(film.getId(),film);
        }
        return result;
    }

    @Override
    public Film add(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[] { "id" });
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, (Integer) film.getMpa().get("id"));
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        if(film.getGenres() != null && film.getGenres().size() > 0){
            putFilmGenres(film.getId(), film.getGenres());
        }
        log.info(LogMessage.FILM_ADD.getMessage() + " {}", film.getId());
        return getFilmById(film.getId()).get();
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().get("id"),
                film.getId()
        );
        if(film.getGenres() != null && film.getGenres().size() > 0){
            updateFilmGenres(film.getId(), film.getGenres());
        } else {
            updateFilmGenres(film.getId(), null);
        }
        return getFilmById(film.getId()).get();
    }

    @Override
    public Collection<Film> getTopLikedFilms(int count) {
        return getAllFilms().entrySet()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getValue().getLikesCount(), f1.getValue().getLikesCount()))
                .limit(count)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                SELECT_FILM_BY_ID, filmId);
        if(rows.next()) {
            log.info(LogMessage.FILM_FOUND.getMessage() + " {}", rows.getString("id"));
            return Optional.of(buildFilmFromRow(rows));
        } else {
            log.info(ErrorMessage.FILM_NOT_FOUND.getMessage(), filmId);
            return Optional.empty();
        }
    }

    @Override
    public void removeFilmById(long filmId) {
        jdbcTemplate.update(DELETE_QUERY, filmId);
    }

    private Set<Genre> getFilmGenresById(long filmId){
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "SELECT tb2.id, tb2.name " +
                        "FROM a_films_genre AS tb1 " +
                        "LEFT JOIN a_genre AS tb2 ON tb1.genre_id = tb2.id " +
                        "WHERE tb1.film_id = ? " +
                        "ORDER BY tb2.id", filmId);
        Set<Genre> result = new LinkedHashSet<>();
        while (rows.next()) {
            Genre genre = new Genre();
            genre.setId(rows.getInt("id"));
            genre.setName(rows.getString("name"));
            result.add(genre);
        }
        if(result.size() == 0){
            return null;
        } else {
            return result;
        }
    }

    private Set<Long> getFilmLikesById(long filmId){
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "SELECT tb1.film_id " +
                        "FROM a_likes AS tb1 " +
                        "WHERE tb1.film_id = ? ", filmId);
        Set<Long> result = new LinkedHashSet<>();
        while (rows.next()) {
            result.add(rows.getLong("film_id"));
        }
        return result;
    }

    @Override
    public void putLike(long filmId, long userId){
        jdbcTemplate.update(INSERT_QUERY_FILM_LIKE, userId, filmId);
        log.info(LogMessage.LIKE_ADD.getMessage() + " {} {}", filmId, userId);
    }
    @Override
    public void deleteLike(long filmId, long userId){
        jdbcTemplate.update(DELETE_QUERY_FILM_LIKE, userId, filmId);
        log.info(LogMessage.LIKE_DELETE.getMessage() + " {} {}", filmId, userId);
    }

    @Override
    public Collection<Mpa> getAllMpa(){
        Set<Mpa> result = new LinkedHashSet <>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_ALL_MPA);
        while(rows.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(rows.getInt("id"));
            mpa.setName(rows.getString("name"));
            result.add(mpa);
        }
        return result;
    }

    @Override
    public Optional<Mpa> getMpaById(int id){
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "SELECT * " +
                        "FROM a_mpa " +
                        "WHERE id = ? LIMIT 1", id);
        if(rows.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(rows.getInt("id"));
            mpa.setName(rows.getString("name"));
            log.info(LogMessage.MPA_FOUND.getMessage() + " {}", rows.getString("id"));
            return Optional.of(mpa);
        } else {
            log.info(ErrorMessage.MPA_NOT_FOUND.getMessage(), id);
            return Optional.empty();
        }
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

    private int countAllRelativeGenres(){
        int count = 0;
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_ALL_RELATIVE_GENRES);
        if(rows.next()) {
            count = rows.getInt("count");
        }
        return count;
    }

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
    public Optional<Genre> getGenreById(int id){
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                "SELECT * " +
                        "FROM a_genre " +
                        "WHERE id = ? LIMIT 1", id);
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

    private Film buildFilmFromRow(SqlRowSet row){
        Film film = new Film();
        film.setId(row.getInt("id"));
        film.setName(row.getString("name"));
        film.setReleaseDate(LocalDate.parse(row.getDate("release_date").toString()));
        film.setDescription(row.getString("descr"));
        film.setDuration(row.getInt("duration"));
        film.setGenres(getFilmGenresById(film.getId()));
        film.setLikeUserIds(getFilmLikesById(film.getId()));
        film.setMpa(new HashMap<String, Object>() {{
            put("id", row.getInt("mpaid"));
            put("name", row.getString("mpaname"));
        }});
        return film;
    }
}
