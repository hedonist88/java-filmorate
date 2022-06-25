package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.helpers.ErrorMessage;
import ru.yandex.practicum.filmorate.helpers.LogMessage;
import ru.yandex.practicum.filmorate.interfaces.MpaStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;
    final String SELECT_ALL_MPA = "SELECT * " +
            "FROM a_mpa ORDER BY id ASC ";

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getAllMpa(){
        Set<Mpa> result = new LinkedHashSet<>();
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
}
