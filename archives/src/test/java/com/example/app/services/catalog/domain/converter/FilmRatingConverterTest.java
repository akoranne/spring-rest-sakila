package com.example.app.services.catalog.domain.converter;

import com.example.app.common.constant.FilmRating;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
class FilmRatingConverterTest {
    @Spy
    private FilmRatingConverter filmRatingConverter;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void convertToDatabaseColumn() {
        // arrange
        final var filmRating = FilmRating.PG;
        final var expected = "PG";

        // act
        final var result = filmRatingConverter.convertToDatabaseColumn(filmRating);

        // assert
        verify(filmRatingConverter, times(1)).convertToDatabaseColumn(filmRating);
        assertEquals(expected, result);
    }

    @Test
    void convertToEntityAttribute() {
        // arrange
        final var filmRating = "NC-17";
        final var expected = FilmRating.NC_17;

        // act
        final var result = filmRatingConverter.convertToEntityAttribute(filmRating);

        // assert
        verify(filmRatingConverter, times(1)).convertToEntityAttribute(filmRating);
        assertEquals(expected, result);
    }
}
