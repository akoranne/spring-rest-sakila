package com.example.app.services.staff.domain.dto;

import com.example.app.common.constant.Country;
import com.example.app.services.location.domain.converter.CountryConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Objects;
import jakarta.persistence.Convert;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;

public class StaffDetailsDto {
    /**
     * The staff detail model provides address and store information for staff members.
     */
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffDetails {
        /**
         * staff id
         */
        @JsonProperty(Fields.id)
        private Integer id;

        /**
         * staff name (first + last)
         */
        @JsonProperty(Fields.name)
        private String name;

        /**
         * address
         */
        @JsonProperty(Fields.address)
        private String address;

        /**
         * postal code
         */
        @JsonProperty(Fields.zipCode)
        private String zipCode;

        /**
         * phone number
         */
        @JsonProperty(Fields.phone)
        private String phone;

        /**
         * city
         */
        @JsonProperty(Fields.city)
        private String city;

        /**
         * country
         */
        @JsonProperty(Fields.country)
        @Convert(converter = CountryConverter.class)
        private Country country;

        /**
         * store id
         */
        @JsonProperty(Fields.sid)
        private Integer sid;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final StaffDetails that = (StaffDetails) o;
            return Objects.equal(id, that.id)
                    && Objects.equal(name, that.name)
                    && Objects.equal(address, that.address)
                    && Objects.equal(zipCode, that.zipCode)
                    && Objects.equal(phone, that.phone)
                    && Objects.equal(city, that.city)
                    && country == that.country
                    && Objects.equal(sid, that.sid);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id, name, address, zipCode, phone, city, country, sid);
        }
    }

    @Relation(collectionRelation = "staffDetailsList", itemRelation = "staffDetails")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffDetailsResponse extends RepresentationModel<StaffDetailsResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private StaffDetails staffDetails;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final StaffDetailsResponse that = (StaffDetailsResponse) o;
            return Objects.equal(staffDetails, that.staffDetails);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), staffDetails);
        }
    }
}
