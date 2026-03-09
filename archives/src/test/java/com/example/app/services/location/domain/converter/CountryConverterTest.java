package com.example.app.services.location.domain.converter;

import com.example.app.common.constant.Country;
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
class CountryConverterTest {
    @Spy
    private CountryConverter countryConverter;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void convertToDatabaseColumn() {
        // arrange
        final var country = Country.SOUTH_KOREA;
        final var expected = 86;

        // act
        final var result = countryConverter.convertToDatabaseColumn(country);

        // assert
        verify(countryConverter, times(1)).convertToDatabaseColumn(country);
        assertEquals(expected, result);
    }

    @Test
    void convertToEntityAttribute() {
        // arrange
        final var country = 103;
        final var expected = Country.UNITED_STATES;

        // act
        final var result = countryConverter.convertToEntityAttribute(country);

        // assert
        verify(countryConverter, times(1)).convertToEntityAttribute(country);
        assertEquals(expected, result);
    }
}
