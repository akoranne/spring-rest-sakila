package com.example.app.services.rental.domain.entity;

import com.example.app.common.domain.dto.Updatable;
import com.example.app.services.customer.domain.entity.CustomerEntity;
import com.example.app.services.staff.domain.entity.StaffEntity;
import com.example.app.services.store.domain.entity.InventoryEntity;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "rental")
@Table(name = "rental", schema = "sakila", indexes = {
        @Index(name = "rental_date", columnList = "rental_date, inventory_id, customer_id", unique = true),
        @Index(name = "idx_fk_inventory_id", columnList = "inventory_id"),
        @Index(name = "idx_fk_customer_id", columnList = "customer_id"),
        @Index(name = "idx_fk_staff_id", columnList = "staff_id")
})
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RentalEntity implements Serializable, Updatable<RentalEntity> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rental_id", columnDefinition = "INT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rentalId;

    @Basic
    @Column(name = "rental_date", columnDefinition = "DATETIME", nullable = false)
    @NotNull
    private LocalDateTime rentalDate;

    @Basic
    @Column(name = "inventory_id", columnDefinition = "MEDIUMINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer inventoryId;

    @Basic
    @Column(name = "customer_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer customerId;

    @Basic
    @Column(name = "return_date", columnDefinition = "DATETIME", nullable = true)
    @ColumnDefault("NULL")
    private LocalDateTime returnDate;

    @Basic
    @Column(name = "staff_id", columnDefinition = "TINYINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer staffId;

    @Basic
    @Column(name = "last_update", columnDefinition = "TIMESTAMP", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @UpdateTimestamp
    @NotNull
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "inventory_id", referencedColumnName = "inventory_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private InventoryEntity inventoryByInventoryId;

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

    @Override
    public void update(RentalEntity entity) {
        this.rentalDate = entity.rentalDate;
        this.inventoryId = entity.inventoryId;
        this.customerId = entity.customerId;
        this.returnDate = entity.returnDate;
        this.staffId = entity.staffId;
        this.lastUpdate = entity.lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RentalEntity that = (RentalEntity) o;
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
        return Objects.hashCode(rentalId, rentalDate, inventoryId, customerId, returnDate, staffId, lastUpdate);
    }
}
