package com.example.app.services.location.domain.dto;

import com.example.app.common.constant.Country;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Objects;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class CityDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class City {
        @JsonProperty(Fields.cityId)
        private Integer cityId;

        @JsonProperty(Fields.city)
        @Size(min = 1, max = 50)
        private String city;

        @JsonProperty(Fields.countryId)
        private Country countryId;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @JsonIgnore
        @JsonProperty(Fields.countryByCountryId)
        @ToString.Exclude
        private CountryDto.Country countryByCountryId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final City that = (City) o;
            return Objects.equal(cityId, that.cityId)
                    && Objects.equal(city, that.city)
                    && countryId == that.countryId
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(cityId, city, countryId, lastUpdate);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CityRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.city)
        @Size(min = 1, max = 50)
        private String city;

        @JsonProperty(Fields.countryId)
        private Country countryId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final CityRequest that = (CityRequest) o;
            return Objects.equal(city, that.city)
                    && countryId == that.countryId;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(city, countryId);
        }
    }

    @Relation(collectionRelation = "cityList", itemRelation = "city")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CityResponse extends RepresentationModel<CityResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private City city;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final CityResponse that = (CityResponse) o;
            return Objects.equal(city, that.city);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), city);
        }
    }
}
