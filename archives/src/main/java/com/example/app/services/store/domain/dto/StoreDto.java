package com.example.app.services.store.domain.dto;

import com.example.app.services.location.domain.dto.AddressDto;
import com.example.app.services.staff.domain.dto.StaffDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Objects;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class StoreDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Store {
        @JsonProperty(Fields.storeId)
        private Integer storeId;

        @JsonProperty(Fields.managerStaffId)
        private Integer managerStaffId;

        @JsonProperty(Fields.addressId)
        private Integer addressId;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @JsonIgnore
        @JsonProperty(Fields.staffByManagerStaffId)
        @ToString.Exclude
        private StaffDto.Staff staffByManagerStaffId;

        @JsonIgnore
        @JsonProperty(Fields.addressByAddressId)
        @ToString.Exclude
        private AddressDto.Address addressByAddressId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Store that = (Store) o;
            return Objects.equal(storeId, that.storeId)
                    && Objects.equal(managerStaffId, that.managerStaffId)
                    && Objects.equal(addressId, that.addressId)
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(storeId, managerStaffId, addressId, lastUpdate);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.managerStaffId)
        private Integer managerStaffId;

        @JsonProperty(Fields.addressId)
        private Integer addressId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final StoreRequest that = (StoreRequest) o;
            return Objects.equal(managerStaffId, that.managerStaffId)
                    && Objects.equal(addressId, that.addressId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(managerStaffId, addressId);
        }
    }

    @Relation(collectionRelation = "storeList", itemRelation = "store")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreResponse extends RepresentationModel<StoreResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private Store store;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final StoreResponse that = (StoreResponse) o;
            return Objects.equal(store, that.store);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), store);
        }
    }
}
