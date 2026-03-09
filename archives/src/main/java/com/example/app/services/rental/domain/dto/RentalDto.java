package com.example.app.services.rental.domain.dto;

import com.example.app.services.customer.domain.dto.CustomerDto;
import com.example.app.services.staff.domain.dto.StaffDto;
import com.example.app.services.store.domain.dto.InventoryDto;
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

public class RentalDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rental {
        @JsonProperty(Fields.rentalId)
        private Integer rentalId;

        @JsonProperty(Fields.rentalDate)
        private LocalDateTime rentalDate;

        @JsonProperty(Fields.inventoryId)
        private Integer inventoryId;

        @JsonProperty(Fields.customerId)
        private Integer customerId;

        @JsonProperty(Fields.returnDate)
        private LocalDateTime returnDate;

        @JsonProperty(Fields.staffId)
        private Integer staffId;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @JsonIgnore
        @JsonProperty(Fields.inventoryByInventoryId)
        @ToString.Exclude
        private InventoryDto.Inventory inventoryByInventoryId;

        @JsonIgnore
        @JsonProperty(Fields.customerByCustomerId)
        @ToString.Exclude
        private CustomerDto.Customer customerByCustomerId;

        @JsonIgnore
        @JsonProperty(Fields.staffByStaffId)
        @ToString.Exclude
        private StaffDto.Staff staffByStaffId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Rental that = (Rental) o;
            return Objects.equal(rentalId, that.rentalId)
                    && Objects.equal(rentalDate, that.rentalDate)
                    && Objects.equal(inventoryId, that.inventoryId)
                    && Objects.equal(customerId, that.customerId)
                    && Objects.equal(returnDate, that.returnDate)
                    && Objects.equal(staffId, that.staffId)
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(rentalId, rentalDate, inventoryId, customerId, returnDate,
                    staffId, lastUpdate);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalCreateRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.rentalDate)
        private LocalDateTime rentalDate;

        @JsonProperty(Fields.storeId)
        private Integer storeId;

        @JsonProperty(Fields.filmId)
        private Integer filmId;

        @JsonProperty(Fields.customerId)
        private Integer customerId;

        @JsonProperty(Fields.staffId)
        private Integer staffId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final RentalCreateRequest that = (RentalCreateRequest) o;
            return Objects.equal(rentalDate, that.rentalDate)
                    && Objects.equal(storeId, that.storeId)
                    && Objects.equal(filmId, that.filmId)
                    && Objects.equal(customerId, that.customerId)
                    && Objects.equal(staffId, that.staffId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(rentalDate, storeId, filmId, customerId, staffId);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalUpdateRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.rentalDate)
        private LocalDateTime rentalDate;

        @JsonProperty(Fields.inventoryId)
        private Integer inventoryId;

        @JsonProperty(Fields.customerId)
        private Integer customerId;

        @JsonProperty(Fields.returnDate)
        private LocalDateTime returnDate;

        @JsonProperty(Fields.staffId)
        private Integer staffId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final RentalUpdateRequest that = (RentalUpdateRequest) o;
            return Objects.equal(rentalDate, that.rentalDate)
                    && Objects.equal(inventoryId, that.inventoryId)
                    && Objects.equal(customerId, that.customerId)
                    && Objects.equal(returnDate, that.returnDate)
                    && Objects.equal(staffId, that.staffId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(rentalDate, inventoryId, customerId, returnDate, staffId);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnCreateRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.inventoryId)
        private Integer inventoryId;

        @JsonProperty(Fields.customerId)
        private Integer customerId;

        @JsonProperty(Fields.returnDate)
        private LocalDateTime returnDate;
    }

    @Relation(collectionRelation = "rentalList", itemRelation = "rental")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RentalResponse extends RepresentationModel<RentalResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private Rental rental;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final RentalResponse that = (RentalResponse) o;
            return Objects.equal(rental, that.rental);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), rental);
        }
    }
}
