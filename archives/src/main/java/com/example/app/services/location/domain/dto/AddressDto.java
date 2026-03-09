package com.example.app.services.location.domain.dto;

import com.example.app.common.domain.serializer.NullToEmptyStringSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AddressDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        @JsonProperty(Fields.addressId)
        private Integer addressId;

        @JsonProperty(Fields.address)
        @Size(min = 1, max = 50)
        private String address;

        @JsonProperty(Fields.address2)
        @JsonSerialize(nullsUsing = NullToEmptyStringSerializer.class)
        @Size(min = 1, max = 50)
        private String address2;

        @JsonProperty(Fields.district)
        @Size(min = 1, max = 20)
        private String district;

        @JsonProperty(Fields.cityId)
        private Integer cityId;

        @JsonProperty(Fields.postalCode)
        @Size(min = 1, max = 10)
        private String postalCode;

        @JsonProperty(Fields.phone)
        @Size(min = 1, max = 20)
        private String phone;

        // @JsonProperty(Fields.location)
        // private Point location;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @JsonIgnore
        @JsonProperty(Fields.cityByCityId)
        @ToString.Exclude
        private CityDto.City cityByCityId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Address that = (Address) o;
            return Objects.equal(addressId, that.addressId)
                    && Objects.equal(address, that.address)
                    && Objects.equal(address2, that.address2)
                    && Objects.equal(district, that.district)
                    && Objects.equal(cityId, that.cityId)
                    && Objects.equal(postalCode, that.postalCode)
                    && Objects.equal(phone, that.phone)
                    // && Objects.equal(location, that.location)
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            // return Objects.hashCode(addressId, address, address2, district, cityId, postalCode, phone, location, lastUpdate);
            return Objects.hashCode(addressId, address, address2, district, cityId, postalCode, phone, lastUpdate);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.address)
        @Size(min = 1, max = 50)
        private String address;

        @JsonProperty(Fields.address2)
        @Size(min = 1, max = 50)
        private String address2;

        @JsonProperty(Fields.district)
        @Size(min = 1, max = 20)
        private String district;

        @JsonProperty(Fields.cityId)
        private Integer cityId;

        @JsonProperty(Fields.postalCode)
        @Size(min = 1, max = 10)
        private String postalCode;

        @JsonProperty(Fields.phone)
        @Size(min = 1, max = 20)
        private String phone;

        // @JsonProperty(Fields.location)
        // private Point location;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final AddressRequest that = (AddressRequest) o;
            return Objects.equal(address, that.address)
                    && Objects.equal(address2, that.address2)
                    && Objects.equal(district, that.district)
                    && Objects.equal(cityId, that.cityId)
                    && Objects.equal(postalCode, that.postalCode)
                    && Objects.equal(phone, that.phone);
            // && Objects.equal(location, that.location);
        }

        @Override
        public int hashCode() {
            // return Objects.hashCode(address, address2, district, cityId, postalCode, phone, location);
            return Objects.hashCode(address, address2, district, cityId, postalCode, phone);
        }
    }

    @Relation(collectionRelation = "addressList", itemRelation = "address")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressResponse extends RepresentationModel<AddressResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private Address address;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final AddressResponse that = (AddressResponse) o;
            return Objects.equal(address, that.address);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), address);
        }
    }
}
