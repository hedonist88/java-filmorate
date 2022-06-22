CREATE TYPE IF NOT EXISTS "friendstatus" AS ENUM (
  'UNCONFIRMED',
  'CONFIRMED'
);

CREATE TABLE IF NOT EXISTS "a_users" (
     "id" SERIAL PRIMARY KEY,
     "email" varchar(50) UNIQUE NOT NULL,
    "login" varchar(30) UNIQUE NOT NULL,
    "name" varchar(50) NOT NULL,
    "birthday" date,
    "cdate" timestamp DEFAULT (now()),
    "udate" timestamp
    );

CREATE TABLE IF NOT EXISTS "a_friends" (
   "user_id" long NOT NULL,
   "friend_id" long NOT NULL,
   "status" friendstatus DEFAULT 'UNCONFIRMED',
   "cdate" timestamp DEFAULT (now())
    );

CREATE TABLE IF NOT EXISTS "a_films" (
     "id" SERIAL PRIMARY KEY,
     "name" varchar(50) NOT NULL,
    "descr" varchar(200),
    "release_date" datetime,
    "duration" int,
    "mpa_id" int,
    "cdate" timestamp DEFAULT (now())
    );

CREATE TABLE IF NOT EXISTS "a_films_genre" (
   "film_id" long PRIMARY KEY,
   "genre_id" int,
   "udate" timestamp
);

CREATE TABLE IF NOT EXISTS "a_genre" (
     "id" SERIAL PRIMARY KEY,
     "name" varchar(50) NOT NULL,
    "descr" varchar
    );

CREATE TABLE IF NOT EXISTS "a_mpa" (
   "id" SERIAL PRIMARY KEY,
   "name" varchar(10) NOT NULL,
    "descr" varchar(200)
    );

CREATE TABLE IF NOT EXISTS "a_likes" (
     "user_id" long,
     "movie_id" long,
     "cdate" timestamp DEFAULT (now())
    );

CREATE INDEX IF NOT EXISTS iusers ON "a_users" ("id", "email", "login");

CREATE INDEX IF NOT EXISTS ifriends ON "a_friends" ("user_id");

CREATE INDEX IF NOT EXISTS ifilms ON "a_films" ("id", "name");

CREATE INDEX IF NOT EXISTS ilikes ON "a_likes" ("user_id", "movie_id");

ALTER TABLE "a_friends" ADD FOREIGN KEY ("user_id") REFERENCES "a_users" ("id");

ALTER TABLE "a_friends" ADD FOREIGN KEY ("friend_id") REFERENCES "a_users" ("id");

ALTER TABLE "a_films" ADD FOREIGN KEY ("mpa_id") REFERENCES "a_mpa" ("id");

ALTER TABLE "a_films_genre" ADD FOREIGN KEY ("film_id") REFERENCES "a_films" ("id");

ALTER TABLE "a_films_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "a_genre" ("id");

ALTER TABLE "a_likes" ADD FOREIGN KEY ("user_id") REFERENCES "a_users" ("id");

ALTER TABLE "a_likes" ADD FOREIGN KEY ("movie_id") REFERENCES "a_films" ("id");
