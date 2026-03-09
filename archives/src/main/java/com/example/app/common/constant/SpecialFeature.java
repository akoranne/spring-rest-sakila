package com.example.app.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum SpecialFeature {
    TRAILERS(Constants.TRAILERS),
    COMMENTARIES(Constants.COMMENTARIES),
    DELETED_SCENES(Constants.DELETED_SCENES),
    BEHIND_THE_SCENES(Constants.BEHIND_THE_SCENES);

    public static final Map<String, SpecialFeature> FEATURE_MAP = Stream.of(SpecialFeature.values())
            .collect(Collectors.toUnmodifiableMap(SpecialFeature::getFeature, Function.identity()));

    private final String feature;

    public static class Constants {
        public static final String TRAILERS = "Trailers";
        public static final String COMMENTARIES = "Commentaries";
        public static final String DELETED_SCENES = "Deleted Scenes";
        public static final String BEHIND_THE_SCENES = "Behind the Scenes";
    }
}
