package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.LikeStorage;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Slf4j
@Qualifier("likeDbStorage")
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    final String SELECT_FILM_LIKES_BY_ID = "SELECT tb1.film_id " +
            "FROM a_likes AS tb1 " +
            "WHERE tb1.film_id = ? ";
    final String INSERT_QUERY_FILM_LIKE = "INSERT INTO a_likes " +
            "(user_id, film_id) VALUES (?, ?)";
    final String DELETE_QUERY_FILM_LIKE = "DELETE FROM a_likes " +
            "WHERE user_id = ? AND film_id = ?";

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public Set<Long> getFilmLikesById(long filmId){
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_FILM_LIKES_BY_ID, filmId);
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

}
