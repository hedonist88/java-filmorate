package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;

    final String INSERT_QUERY = "INSERT INTO a_films " +
            "(name, descr, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
    final String UPDATE_QUERY = "UPDATE a_films SET name = ?, descr = ?," +
            "release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    final String SELECT_ALL = "SELECT tb1.*, tb2.id AS mpaid, tb2.name AS mpaname " +
            "FROM a_films AS tb1 " +
            "LEFT JOIN a_mpa AS tb2 ON tb2.id = tb1.mpa_id";
    final String SELECT_FILM_BY_ID ="SELECT tb1.*, tb2.id AS mpaid, tb2.name AS mpaname " +
            "FROM a_films AS tb1 " +
            "LEFT JOIN a_mpa AS tb2 ON tb2.id = tb1.mpa_id " +
            "WHERE tb1.id = ? ";
    final String DELETE_QUERY = "DELETE FROM a_films WHERE id = ?";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage,
                         LikeDbStorage likeDbStorage, MpaDbStorage mpaDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
        this.mpaDbStorage = mpaDbStorage;
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
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("a_films").usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("descr", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpa_id", film.getMpa().get("id"));
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        film.setId(num.longValue());
        //тут сохрани жанры и лайки п сводные таблицы
        /*
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
       */
        if(film.getGenres() != null && film.getGenres().size() > 0){
            genreDbStorage.putFilmGenres(film.getId(), film.getGenres());
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
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            genreDbStorage.updateFilmGenres(film.getId(), film.getGenres());
        } else {
            genreDbStorage.updateFilmGenres(film.getId(), null);
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

    private Film buildFilmFromRow(SqlRowSet row){
        Film film = new Film();
        film.setId(row.getInt("id"));
        film.setName(row.getString("name"));
        film.setReleaseDate(LocalDate.parse(row.getDate("release_date").toString()));
        film.setDescription(row.getString("descr"));
        film.setDuration(row.getInt("duration"));
        film.setGenres(genreDbStorage.getFilmGenresById(film.getId()));
        film.setLikeUserIds(likeDbStorage.getFilmLikesById(film.getId()));
        film.setMpa(new HashMap<String, Object>() {{
            put("id", row.getInt("mpaid"));
            put("name", row.getString("mpaname"));
        }});
        return film;
    }
}
