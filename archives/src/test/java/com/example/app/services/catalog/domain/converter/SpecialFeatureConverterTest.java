package com.example.app.services.catalog.domain.converter;

import com.example.app.common.constant.SpecialFeature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
class SpecialFeatureConverterTest {
    @Spy
    private SpecialFeatureConverter specialFeatureConverter;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void convertToDatabaseColumn() {
        // arrange
        final var specialFeatures = EnumSet.of(SpecialFeature.TRAILERS, SpecialFeature.COMMENTARIES);
        final var expected = "Trailers,Commentaries";

        // act
        final var result = specialFeatureConverter.convertToDatabaseColumn(specialFeatures);

        // assert
        verify(specialFeatureConverter, times(1)).convertToDatabaseColumn(specialFeatures);
        assertEquals(expected, result);
    }

    @Test
    void convertToEntityAttribute() {
        // arrange
        final var specialFeatures = "Deleted Scenes,Behind the Scenes";
        final var expected = EnumSet.of(SpecialFeature.DELETED_SCENES, SpecialFeature.BEHIND_THE_SCENES);

        // act
        final var result = specialFeatureConverter.convertToEntityAttribute(specialFeatures);

        // assert
        verify(specialFeatureConverter, times(1)).convertToEntityAttribute(specialFeatures);
        assertEquals(expected, result);
    }
}
