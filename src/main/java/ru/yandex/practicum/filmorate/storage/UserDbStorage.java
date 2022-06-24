package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    final String INSERT_QUERY = "INSERT INTO a_users " +
            "(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    final String UPDATE_QUERY = "UPDATE a_users SET email = ?, login = ?," +
            "name = ?, birthday = ?, udate = ? WHERE id = ?";
    final String DELETE_QUERY = "DELETE FROM a_users WHERE id = ?";
    final String SELECT_ALL = "SELECT tb1.* FROM a_users AS tb1";

    final String INSERT_QUERY_FRIENDS =
            "INSERT INTO a_friends " +
            "(user_id, friend_id, status) VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE user_id = ?, friend_id = ?, status = ?";
    final String DELETE_QUERY_FRIENDS = "DELETE FROM a_friends " +
            "WHERE (user_id = ? AND friend_id = ?)";

    final String SELECT_USER_BY_ID ="SELECT tb1.* " +
            "FROM a_users AS tb1 " +
            "WHERE tb1.id = ? ";
    final String SELECT_USER_BY_EMAIL ="SELECT tb1.* " +
            "FROM a_users AS tb1 " +
            "WHERE tb1.email = ? ";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Long, User> getAllUsers() {
        Map<Long, User> result = new HashMap<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet(SELECT_ALL);
        while(rows.next()) {
            User user = buildUserFromRow(rows);
            result.put(user.getId(),user);
        }
        return result;
    }

    @Override
    public Optional<User> getUserById(long userId) {
        User user;
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                SELECT_USER_BY_ID, userId);
        if(rows.next()) {
            log.info(LogMessage.USER_FOUND.getMessage() + " {}", rows.getString("id"));
            user = buildUserFromRow(rows);
        } else {
            log.info(ErrorMessage.USERS_NOT_FOUND.getMessage(), userId);
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("a_users").usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        Number num = jdbcInsert.executeAndReturnKey(parameters);
        user.setId(num.longValue());
        /*
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[] { "id" });
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());*/
        log.info(LogMessage.USER_ADD.getMessage() + " {}", user.getId());
        return user;
    }

    @Override
    public void putFriendsRelation(long userId, long friendId, FriendStatus status){
        jdbcTemplate.update(
                INSERT_QUERY_FRIENDS,
                userId,
                friendId,
                status.name(),
                userId,
                friendId,
                status.name()
        );
    }

    @Override
    public void deleteFriendsRelation(long userId, long friendId){
        jdbcTemplate.update(
                DELETE_QUERY_FRIENDS,
                userId,
                friendId
        );
        jdbcTemplate.update(
                DELETE_QUERY_FRIENDS,
                friendId,
                userId
        );
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                Timestamp.from(Instant.now()).toString(),
                user.getId()
        );
        return user;
    }

    @Override
    public void removeUserById(long userId) {
        jdbcTemplate.update(DELETE_QUERY, userId);
    }

    private Map<Long, FriendStatus> getUserFriendsById(long userId){
        Map<Long, FriendStatus> friends = new HashMap<>();
        SqlRowSet frows = jdbcTemplate.queryForRowSet(
                "SELECT * FROM a_friends WHERE user_id = ?", userId);
        while (frows.next()) {
            if(FriendStatus.valueOf(frows.getString("status"))
                    .equals(FriendStatus.CONFIRMED)) {
                friends.put(
                        frows.getLong("friend_id"),
                        FriendStatus.valueOf(frows.getString("status")));
            }
        }
        return friends;
    }

    private User buildUserFromRow(SqlRowSet row){
        User user = new User();
        user.setId(row.getInt("id"));
        user.setEmail(row.getString("email"));
        user.setLogin(row.getString("login"));
        user.setName(row.getString("name"));
        user.setBirthday(LocalDate.parse(row.getDate("birthday").toString()));
        user.setFriendsIds(getUserFriendsById(row.getInt("id")));
        return user;
    }
}
