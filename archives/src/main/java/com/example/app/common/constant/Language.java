package com.example.app.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Language {
    NONE(0, Constants.NONE),
    ENGLISH(1, Constants.ENGLISH),
    ITALIAN(2, Constants.ITALIAN),
    JAPANESE(3, Constants.JAPANESE),
    MANDARIN(4, Constants.MANDARIN),
    FRENCH(5, Constants.FRENCH),
    GERMAN(6, Constants.GERMAN);

    public static final Map<Integer, Language> ID_MAP = Stream.of(Language.values())
            .collect(Collectors.toUnmodifiableMap(Language::getId, Function.identity()));
    public static final Map<String, Language> LANGUAGE_MAP = Stream.of(Language.values())
            .collect(Collectors.toUnmodifiableMap(Language::getLanguage, Function.identity()));

    private final Integer id;
    private final String language;

    public static class Constants {
        public static final String NONE = "-";
        public static final String ENGLISH = "English";
        public static final String ITALIAN = "Italian";
        public static final String JAPANESE = "Japanese";
        public static final String MANDARIN = "Mandarin";
        public static final String FRENCH = "French";
        public static final String GERMAN = "German";
    }
}
