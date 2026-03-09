package com.example.app.services.customer.domain.entity;

import com.example.app.common.domain.dto.Updatable;
import com.example.app.common.domain.entity.FullName;
import com.example.app.services.auth.domain.entity.AuthorityEntity;
import com.example.app.services.location.domain.entity.AddressEntity;
import com.example.app.services.store.domain.entity.StoreEntity;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "customer")
@Table(name = "customer", schema = "sakila", indexes = {
        @Index(name = "idx_fk_store_id", columnList = "store_id"),
        @Index(name = "idx_fk_address_id", columnList = "address_id"),
        @Index(name = "idx_fk_authority_id", columnList = "authority_id"),
        @Index(name = "idx_last_name", columnList = "last_name")
})
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CustomerEntity implements Serializable, Updatable<CustomerEntity> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "customer_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;

    @Basic
    @Column(name = "store_id", columnDefinition = "TINYINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer storeId;

    @Embedded
    private FullName fullName;

    @Basic
    @Column(name = "address_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer addressId;

    @Basic
    @Column(name = "active", columnDefinition = "BOOLEAN", nullable = false)
    @ColumnDefault("TRUE")
    @NotNull
    private Boolean active;

    @Basic
    @Column(name = "authority_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer authorityId;

    @Basic
    @Column(name = "create_date", columnDefinition = "DATETIME", nullable = false)
    @NotNull
    private LocalDateTime createDate;

    @Basic
    @Column(name = "last_update", columnDefinition = "TIMESTAMP", nullable = true)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "store_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private StoreEntity storeByStoreId;

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "address_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private AddressEntity addressByAddressId;

    @ManyToOne
    @JoinColumn(name = "authority_id", referencedColumnName = "authority_id", nullable = false)
    @ToString.Exclude
    private AuthorityEntity authorityByAuthorityId;

    @Override
    public void update(CustomerEntity entity) {
        this.storeId = entity.getStoreId();
        this.fullName = entity.getFullName();
        this.addressId = entity.getAddressId();
        this.active = entity.getActive();
        this.authorityId = entity.getAuthorityId();
        this.createDate = entity.getCreateDate();
        this.lastUpdate = entity.getLastUpdate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CustomerEntity that = (CustomerEntity) o;
        return Objects.equal(customerId, that.customerId)
                && Objects.equal(storeId, that.storeId)
                && Objects.equal(fullName, that.fullName)
                && Objects.equal(addressId, that.addressId)
                && Objects.equal(active, that.active)
                && Objects.equal(authorityId, that.authorityId)
                && Objects.equal(createDate, that.createDate)
                && Objects.equal(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(customerId, storeId, fullName, addressId, active, authorityId,
                createDate, lastUpdate);
    }
}
