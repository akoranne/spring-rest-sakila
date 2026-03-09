package com.example.app.services.location.domain.converter;

import com.example.app.common.constant.Language;
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
class LanguageConverterTest {
    @Spy
    private LanguageConverter languageConverter;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void convertToDatabaseColumn() {
        // arrange
        final var language = Language.ENGLISH;
        final var expected = 1;

        // act
        final var result = languageConverter.convertToDatabaseColumn(language);

        // assert
        verify(languageConverter, times(1)).convertToDatabaseColumn(language);
        assertEquals(expected, result);
    }

    @Test
    void convertToEntityAttribute() {
        // arrange
        final var language = 2;
        final var expected = Language.ITALIAN;

        // act
        final var result = languageConverter.convertToEntityAttribute(language);

        // assert
        verify(languageConverter, times(1)).convertToEntityAttribute(language);
        assertEquals(expected, result);
    }
}
