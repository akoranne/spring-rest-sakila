package com.example.app.services.location.domain.dto;

import com.example.app.services.location.domain.converter.CountryConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

public class CountryDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Country {
        @JsonProperty(Fields.countryId)
        @Convert(converter = CountryConverter.class)
        private com.example.app.common.constant.Country countryId;

        @JsonProperty(Fields.country)
        @Size(min = 1, max = 50)
        private String country;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Country that = (Country) o;
            return countryId == that.countryId
                    && country == that.country
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(countryId, country, lastUpdate);
        }
    }
}
