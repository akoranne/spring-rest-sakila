package com.example.app.services.staff.domain.entity;

import com.example.app.common.domain.dto.Updatable;
import com.example.app.common.domain.entity.FullName;
import com.example.app.services.auth.domain.entity.AuthorityEntity;
import com.example.app.services.location.domain.entity.AddressEntity;
import com.example.app.services.store.domain.entity.StoreEntity;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "staff")
@Table(name = "staff", schema = "sakila", indexes = {
        @Index(name = "idx_fk_store_id", columnList = "store_id"),
        @Index(name = "idx_fk_address_id", columnList = "address_id"),
        @Index(name = "idx_fk_authority_id", columnList = "authority_id")
})
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StaffEntity implements Serializable, Updatable<StaffEntity> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "staff_id", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer staffId;

    @Embedded
    private FullName fullName;

    @Basic
    @Column(name = "address_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer addressId;

    // @Basic
    // @Column(name = "picture", columnDefinition = "BLOB", nullable = true)
    // @ColumnDefault("NULL")
    // @Lob
    // private byte[] picture;

    @Basic
    @Column(name = "store_id", columnDefinition = "TINYINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer storeId;

    @Basic
    @Column(name = "active", columnDefinition = "BOOLEAN", nullable = false)
    @ColumnDefault("TRUE")
    @NotNull
    private Boolean active;

    @Basic
    @Column(name = "username", length = 16, nullable = false)
    @NotNull
    @Size(min = 1, max = 16)
    private String username;

    @Basic
    @Column(name = "authority_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer authorityId;

    @Basic
    @Column(name = "last_update", columnDefinition = "TIMESTAMP", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @UpdateTimestamp
    @NotNull
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "address_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private AddressEntity addressByAddressId;

    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "store_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private StoreEntity storeByStoreId;

    @ManyToOne
    @JoinColumn(name = "authority_id", referencedColumnName = "authority_id", nullable = false)
    @ToString.Exclude
    private AuthorityEntity authorityByAuthorityId;

    @Override
    public void update(StaffEntity entity) {
        this.fullName = entity.fullName;
        this.addressId = entity.addressId;
        // this.picture = entity.picture;
        this.storeId = entity.storeId;
        this.active = entity.active;
        this.username = entity.username;
        this.authorityId = entity.getAuthorityId();
        this.lastUpdate = entity.lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StaffEntity that = (StaffEntity) o;
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
