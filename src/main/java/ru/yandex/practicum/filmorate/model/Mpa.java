package ru.yandex.practicum.filmorate.model;

public enum Mpa {
    G("у фильма нет возрастных ограничений"),
    PG("детям рекомендуется смотреть фильм с родителями"),
    PG13("детям до 13 лет просмотр не желателен"),
    R("лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
    NC17("лицам до 18 лет просмотр запрещён");

    private final String descr;

    private Mpa(String descr) {
        this.descr = descr;
    }

    public String getTitle() {
        return descr;
    }
}
