package com.example.app.services.payment.domain.entity;

import com.example.app.common.domain.dto.Updatable;
import com.example.app.services.customer.domain.entity.CustomerEntity;
import com.example.app.services.rental.domain.entity.RentalEntity;
import com.example.app.services.staff.domain.entity.StaffEntity;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "payment")
@Table(name = "payment", schema = "sakila", indexes = {
        @Index(name = "idx_fk_staff_id", columnList = "staff_id"),
        @Index(name = "idx_fk_customer_id", columnList = "customer_id")
})
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentEntity implements Serializable, Updatable<PaymentEntity> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "payment_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @Basic
    @Column(name = "customer_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer customerId;

    @Basic
    @Column(name = "staff_id", columnDefinition = "TINYINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer staffId;

    @Basic
    @Column(name = "rental_id", columnDefinition = "INT", nullable = true,
            insertable = false, updatable = false)
    @ColumnDefault("NULL")
    private Integer rentalId;

    @Basic
    @Column(name = "amount", columnDefinition = "DECIMAL(5,2)", precision = 2, nullable = false)
    @NotNull
    private BigDecimal amount;

    @Basic
    @Column(name = "payment_date", columnDefinition = "DATETIME", nullable = false)
    @NotNull
    private LocalDateTime paymentDate;

    @Basic
    @Column(name = "last_update", columnDefinition = "TIMESTAMP", nullable = true)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private CustomerEntity customerByCustomerId;

    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "staff_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private StaffEntity staffByStaffId;

    @ManyToOne
    @JoinColumn(name = "rental_id", referencedColumnName = "rental_id")
    @ToString.Exclude
    private RentalEntity rentalByRentalId;

    @Override
    public void update(PaymentEntity entity) {
        this.customerId = entity.customerId;
        this.staffId = entity.staffId;
        this.rentalId = entity.rentalId;
        this.amount = entity.amount;
        this.paymentDate = entity.paymentDate;
        this.lastUpdate = entity.lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PaymentEntity that = (PaymentEntity) o;
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
