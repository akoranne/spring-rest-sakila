package com.example.app.services.customer.domain.dto;

import com.example.app.common.domain.dto.FullName;
import com.example.app.services.auth.domain.dto.AuthorityDto;
import com.example.app.services.location.domain.dto.AddressDto;
import com.example.app.services.store.domain.dto.StoreDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Objects;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class CustomerDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customer {
        @JsonProperty(Fields.customerId)
        private Integer customerId;

        @JsonProperty(Fields.storeId)
        private Integer storeId;

        @JsonUnwrapped
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "fullName")
        private FullName fullName;

        @JsonProperty(Fields.addressId)
        private Integer addressId;

        @JsonProperty(Fields.active)
        private Boolean active;

        @JsonProperty(Fields.authorityId)
        private Integer authorityId;

        @JsonProperty(Fields.createDate)
        private LocalDateTime createDate;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @JsonIgnore
        @JsonProperty(Fields.storeByStoreId)
        @ToString.Exclude
        private StoreDto.Store storeByStoreId;

        @JsonIgnore
        @JsonProperty(Fields.addressByAddressId)
        @ToString.Exclude
        private AddressDto.Address addressByAddressId;

        @JsonIgnore
        @JsonProperty(Fields.authorityByAuthorityId)
        @ToString.Exclude
        private AuthorityDto.Authority authorityByAuthorityId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Customer that = (Customer) o;
            return Objects.equal(customerId, that.customerId)
                    && Objects.equal(storeId, that.storeId)
                    && Objects.equal(fullName, that.fullName)
                    && Objects.equal(addressId, that.addressId)
                    && Objects.equal(active, that.active)
                    && Objects.equal(createDate, that.createDate)
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(customerId, storeId, fullName, addressId, active, createDate, lastUpdate);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.storeId)
        private Integer storeId;

        @JsonProperty(Fields.firstName)
        private String firstName;

        @JsonProperty(Fields.lastName)
        private String lastName;

        @JsonProperty(Fields.addressId)
        private Integer addressId;

        @JsonProperty(Fields.active)
        private Boolean active;

        @JsonProperty(Fields.authorityId)
        private Integer authorityId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final CustomerRequest that = (CustomerRequest) o;
            return Objects.equal(storeId, that.storeId)
                    && Objects.equal(firstName, that.firstName)
                    && Objects.equal(lastName, that.lastName)
                    && Objects.equal(addressId, that.addressId)
                    && Objects.equal(active, that.active)
                    && Objects.equal(authorityId, that.authorityId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(storeId, firstName, lastName, addressId, active, authorityId);
        }
    }

    @Relation(collectionRelation = "customerList", itemRelation = "customer")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerResponse extends RepresentationModel<CustomerResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private Customer customer;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final CustomerResponse that = (CustomerResponse) o;
            return Objects.equal(customer, that.customer);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), customer);
        }
    }
}
