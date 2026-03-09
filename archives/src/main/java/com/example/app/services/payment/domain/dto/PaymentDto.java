package com.example.app.services.payment.domain.dto;

import com.example.app.services.customer.domain.dto.CustomerDto;
import com.example.app.services.rental.domain.dto.RentalDto;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment {
        @JsonProperty(Fields.paymentId)
        private Integer paymentId;

        @JsonProperty(Fields.customerId)
        private Integer customerId;

        @JsonProperty(Fields.staffId)
        private Integer staffId;

        @JsonProperty(Fields.rentalId)
        private Integer rentalId;

        @JsonProperty(Fields.amount)
        private BigDecimal amount;

        @JsonProperty(Fields.paymentDate)
        private LocalDateTime paymentDate;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @JsonIgnore
        @JsonProperty(Fields.customerByCustomerId)
        @ToString.Exclude
        private CustomerDto.Customer customerByCustomerId;

        @JsonIgnore
        @JsonProperty(Fields.staffByStaffId)
        @ToString.Exclude
        private StaffDto.Staff staffByStaffId;

        @JsonIgnore
        @JsonProperty(Fields.rentalByRentalId)
        @ToString.Exclude
        private RentalDto.Rental rentalByRentalId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Payment that = (Payment) o;
            return Objects.equal(paymentId, that.paymentId)
                    && Objects.equal(customerId, that.customerId)
                    && Objects.equal(staffId, that.staffId)
                    && Objects.equal(rentalId, that.rentalId)
                    && Objects.equal(amount, that.amount)
                    && Objects.equal(paymentDate, that.paymentDate)
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(paymentId, customerId, staffId, rentalId, amount,
                    paymentDate, lastUpdate);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonProperty(Fields.customerId)
        private Integer customerId;

        @JsonProperty(Fields.staffId)
        private Integer staffId;

        @JsonProperty(Fields.rentalId)
        private Integer rentalId;

        @JsonProperty(Fields.amount)
        private BigDecimal amount;

        @JsonProperty(Fields.paymentDate)
        private LocalDateTime paymentDate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final PaymentRequest that = (PaymentRequest) o;
            return Objects.equal(customerId, that.customerId)
                    && Objects.equal(staffId, that.staffId)
                    && Objects.equal(rentalId, that.rentalId)
                    && Objects.equal(amount, that.amount)
                    && Objects.equal(paymentDate, that.paymentDate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(customerId, staffId, rentalId, amount, paymentDate);
        }
    }

    @Relation(collectionRelation = "paymentList", itemRelation = "payment")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse extends RepresentationModel<PaymentResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private Payment payment;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final PaymentResponse that = (PaymentResponse) o;
            return Objects.equal(payment, that.payment);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), payment);
        }
    }
}
