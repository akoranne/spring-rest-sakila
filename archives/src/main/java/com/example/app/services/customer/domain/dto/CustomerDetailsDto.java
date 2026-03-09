package com.example.app.services.customer.domain.dto;

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

public class CustomerDetailsDto {
    /**
     * The customer detail model provides a list of customers, with name and address.
     */
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDetails {
        /**
         * customer id
         */
        @JsonProperty(Fields.id)
        private Integer id;

        /**
         * customer name (first + last)
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
         * notes (whether the customer is active)
         */
        @JsonProperty(Fields.notes)
        private String notes;

        /**
         * store id
         */
        @JsonProperty(Fields.sid)
        private Integer sid;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final CustomerDetails that = (CustomerDetails) o;
            return Objects.equal(id, that.id)
                    && Objects.equal(name, that.name)
                    && Objects.equal(address, that.address)
                    && Objects.equal(zipCode, that.zipCode)
                    && Objects.equal(phone, that.phone)
                    && Objects.equal(city, that.city)
                    && country == that.country
                    && Objects.equal(notes, that.notes)
                    && Objects.equal(sid, that.sid);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id, name, address, zipCode, phone, city, country, notes, sid);
        }
    }

    @Relation(collectionRelation = "customerDetailsList", itemRelation = "customerDetails")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDetailsResponse extends RepresentationModel<CustomerDetailsResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private CustomerDetails customerDetailsModel;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final CustomerDetailsResponse that = (CustomerDetailsResponse) o;
            return Objects.equal(customerDetailsModel, that.customerDetailsModel);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), customerDetailsModel);
        }
    }
}
