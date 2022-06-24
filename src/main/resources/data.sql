-- Test clear
--DELETE FROM a_mpa;
--DELETE FROM a_users;
--DELETE FROM a_films;
--DELETE FROM "a_genre";

INSERT INTO "a_mpa" ("name", "descr")
VALUES
    --('NORATING','Рейтинг не указан'),
    ('G','у фильма нет возрастных ограничений'),
    ('PG','детям рекомендуется смотреть фильм с родителями'),
    ('PG-13','детям до 13 лет просмотр не желателен'),
    ('R','лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
    ('NC-17','лицам до 18 лет просмотр запрещён');

INSERT INTO "a_genre" ("name")
VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');

--INSERT INTO "a_films" ("name", "descr", "release_date", "duration")
--VALUES ('Pulp Fiction','Несколько связанных историй из жизни бандитов.',
--        '1994-05-21',154);




