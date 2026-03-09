package com.example.app.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum FilmRating {
    NONE(Constants.NONE),
    G(Constants.G),
    PG(Constants.PG),
    PG_13(Constants.PG_13),
    R(Constants.R),
    NC_17(Constants.NC_17);

    public static final Map<String, FilmRating> RATING_MAP = Stream.of(FilmRating.values())
            .collect(Collectors.toUnmodifiableMap(FilmRating::getRating, Function.identity()));

    private final String rating;

    public static class Constants {
        public static final String NONE = "-";
        public static final String G = "G";
        public static final String PG = "PG";
        public static final String PG_13 = "PG-13";
        public static final String R = "R";
        public static final String NC_17 = "NC-17";
    }
}
