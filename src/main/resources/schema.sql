CREATE TYPE "a_friend_status" AS ENUM (
  'CONFIRMED',
  'UNCONFIRMED'
);

CREATE TABLE "a_users" (
                           "id" SERIAL PRIMARY KEY,
                           "email" varchar(50) UNIQUE NOT NULL,
                           "login" varchar(30) UNIQUE NOT NULL,
                           "name" varchar(50) NOT NULL,
                           "birthday" date,
                           "cdate" timestamp DEFAULT (now()),
                           "udate" timestamp
);

CREATE TABLE "a_friends" (
                             "user_id" long NOT NULL,
                             "friend_id" long NOT NULL,
                             "status" a_friend_status,
                             "cdate" timestamp DEFAULT (now())
);

CREATE TABLE "a_films" (
                           "id" SERIAL PRIMARY KEY,
                           "name" varchar(50) NOT NULL,
                           "descr" varchar(200),
                           "release_date" datetime,
                           "duration" int,
                           "mpa_id" int,
                           "cdate" timestamp DEFAULT (now())
);

CREATE TABLE "a_films_genre" (
                                 "film_id" long PRIMARY KEY,
                                 "genre_id" long,
                                 "udate" timestamp
);

CREATE TABLE "a_genre" (
                           "id" SERIAL PRIMARY KEY,
                           "name" varchar(50) NOT NULL,
                           "descr" varchar
);

CREATE TABLE "a_mpa" (
                         "id" SERIAL PRIMARY KEY,
                         "name" varchar(10) NOT NULL,
                         "descr" varchar(200)
);

CREATE TABLE "a_likes" (
                           "user_id" long,
                           "movie_id" long,
                           "cdate" timestamp DEFAULT (now())
);

CREATE INDEX ON "a_users" ("id", "email", "login");

CREATE INDEX ON "a_friends" ("user_id");

CREATE INDEX ON "a_films" ("id", "name");

CREATE INDEX ON "a_likes" ("user_id", "movie_id");

ALTER TABLE "a_friends" ADD FOREIGN KEY ("user_id") REFERENCES "a_users" ("id");

ALTER TABLE "a_friends" ADD FOREIGN KEY ("friend_id") REFERENCES "a_users" ("id");

CREATE TABLE "a_mpa_a_films" (
                                 "a_mpa_id" long NOT NULL,
                                 "a_films_mpa_id" int NOT NULL,
                                 PRIMARY KEY ("a_mpa_id", "a_films_mpa_id")
);

ALTER TABLE "a_mpa_a_films" ADD FOREIGN KEY ("a_mpa_id") REFERENCES "a_mpa" ("id");

ALTER TABLE "a_mpa_a_films" ADD FOREIGN KEY ("a_films_mpa_id") REFERENCES "a_films" ("mpa_id");


ALTER TABLE "a_films_genre" ADD FOREIGN KEY ("film_id") REFERENCES "a_films" ("id");

CREATE TABLE "a_genre_a_films_genre" (
                                         "a_genre_id" long NOT NULL,
                                         "a_films_genre_genre_id" long NOT NULL,
                                         PRIMARY KEY ("a_genre_id", "a_films_genre_genre_id")
);

ALTER TABLE "a_genre_a_films_genre" ADD FOREIGN KEY ("a_genre_id") REFERENCES "a_genre" ("id");

ALTER TABLE "a_genre_a_films_genre" ADD FOREIGN KEY ("a_films_genre_genre_id") REFERENCES "a_films_genre" ("genre_id");


ALTER TABLE "a_likes" ADD FOREIGN KEY ("user_id") REFERENCES "a_users" ("id");

ALTER TABLE "a_likes" ADD FOREIGN KEY ("movie_id") REFERENCES "a_films" ("id");
