package com.example.app.services.staff.domain.dto;

import com.example.app.common.domain.dto.FullName;
import com.example.app.services.auth.domain.dto.AuthorityDto;
import com.example.app.services.location.domain.dto.AddressDto;
import com.example.app.services.store.domain.dto.StoreDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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

public class StaffDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Staff {
        @JsonProperty(Fields.staffId)
        private Integer staffId;

        @JsonUnwrapped
        @JsonTypeInfo(use = JsonTypeInfo.Id.NONE, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "fullName")
        private FullName fullName;

        @JsonProperty(Fields.addressId)
        private Integer addressId;

        // @JsonProperty(Fields.picture)
        // private byte[] picture;

        @JsonProperty(Fields.storeId)
        private Integer storeId;

        @JsonProperty(Fields.active)
        private Boolean active;

        @JsonProperty(Fields.username)
        @Size(min = 1, max = 16)
        private String username;

        @JsonProperty(Fields.authorityId)
        private Integer authorityId;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @JsonIgnore
        @JsonProperty(Fields.addressByAddressId)
        @ToString.Exclude
        private AddressDto.Address addressByAddressId;

        @JsonIgnore
        @JsonProperty(Fields.storeByStoreId)
        @ToString.Exclude
        private StoreDto.Store storeByStoreId;

        @JsonIgnore
        @JsonProperty(Fields.authorityByAuthorityId)
        @ToString.Exclude
        private AuthorityDto.Authority authorityByAuthorityId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Staff that = (Staff) o;
            return Objects.equal(staffId, that.staffId)
                    && Objects.equal(fullName, that.fullName)
                    && Objects.equal(addressId, that.addressId)
                    // && Objects.equal(picture, that.picture)
                    && Objects.equal(storeId, that.storeId)
                    && Objects.equal(active, that.active)
                    && Objects.equal(username, that.username)
                    && Objects.equal(authorityId, that.authorityId)
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            // return Objects.hashCode(staffId, fullName, addressId, picture,
            //         storeId, active, username, authorityId, lastUpdate);
            return Objects.hashCode(staffId, fullName, addressId,
                    storeId, active, username, authorityId, lastUpdate);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.firstName)
        private String firstName;

        @JsonProperty(Fields.lastName)
        private String lastName;

        @JsonProperty(Fields.addressId)
        private Integer addressId;

        // @JsonProperty(Fields.picture)
        // private byte[] picture;

        @JsonProperty(Fields.storeId)
        private Integer storeId;

        @JsonProperty(Fields.active)
        private Boolean active;

        @JsonProperty(Fields.username)
        @Size(min = 1, max = 16)
        private String username;

        @JsonProperty(Fields.authorityId)
        private Integer authorityId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final StaffRequest that = (StaffRequest) o;
            return Objects.equal(firstName, that.firstName)
                    && Objects.equal(lastName, that.lastName)
                    && Objects.equal(addressId, that.addressId)
                    // && Objects.equal(picture, that.picture)
                    && Objects.equal(storeId, that.storeId)
                    && Objects.equal(active, that.active)
                    && Objects.equal(username, that.username)
                    && Objects.equal(authorityId, that.authorityId);
        }

        @Override
        public int hashCode() {
            // return Objects.hashCode(firstName, lastName, addressId, picture, storeId, active, username, authorityId);
            return Objects.hashCode(firstName, lastName, addressId, storeId, active, username, authorityId);
        }
    }

    @Relation(collectionRelation = "staffList", itemRelation = "staff")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StaffResponse extends RepresentationModel<StaffResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private Staff staff;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final StaffResponse that = (StaffResponse) o;
            return Objects.equal(staff, that.staff);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), staff);
        }
    }
}
