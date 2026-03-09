package com.example.app.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Category {
    NONE(0, Constants.NONE),
    ACTION(1, Constants.ACTION),
    ANIMATION(2, Constants.ANIMATION),
    CHILDREN(3, Constants.CHILDREN),
    CLASSICS(4, Constants.CLASSICS),
    COMEDY(5, Constants.COMEDY),
    DOCUMENTARY(6, Constants.DOCUMENTARY),
    DRAMA(7, Constants.DRAMA),
    FAMILY(8, Constants.FAMILY),
    FOREIGN(9, Constants.FOREIGN),
    GAMES(10, Constants.GAMES),
    HORROR(11, Constants.HORROR),
    MUSIC(12, Constants.MUSIC),
    NEW(13, Constants.NEW),
    SCI_FI(14, Constants.SCI_FI),
    SPORTS(15, Constants.SPORTS),
    TRAVEL(16, Constants.TRAVEL);

    public static final Map<Integer, Category> ID_MAP = Stream.of(Category.values())
            .collect(Collectors.toUnmodifiableMap(Category::getId, Function.identity()));
    public static final Map<String, Category> CATEGORY_MAP = Stream.of(Category.values())
            .collect(Collectors.toUnmodifiableMap(Category::getCategory, Function.identity()));
    public static final Map<String, Category> CATEGORY_LOWER_MAP = Stream.of(Category.values())
            .collect(Collectors.toUnmodifiableMap(category -> category.getCategory().toLowerCase(), Function.identity()));

    private final Integer id;
    private final String category;

    public static class Constants {
        public static final String NONE = "-";
        public static final String ACTION = "Action";
        public static final String ANIMATION = "Animation";
        public static final String CHILDREN = "Children";
        public static final String CLASSICS = "Classics";
        public static final String COMEDY = "Comedy";
        public static final String DOCUMENTARY = "Documentary";
        public static final String DRAMA = "Drama";
        public static final String FAMILY = "Family";
        public static final String FOREIGN = "Foreign";
        public static final String GAMES = "Games";
        public static final String HORROR = "Horror";
        public static final String MUSIC = "Music";
        public static final String NEW = "New";
        public static final String SCI_FI = "Sci-Fi";
        public static final String SPORTS = "Sports";
        public static final String TRAVEL = "Travel";
    }
}
