package com.example.app.services.location.domain.entity;

import com.example.app.common.domain.dto.Updatable;
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

@Entity(name = "address")
@Table(name = "address", schema = "sakila", indexes = {
        @Index(name = "idx_fk_city_id", columnList = "city_id")
        // @Index(name = "idx_location", columnList = "location")
})
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AddressEntity implements Serializable, Updatable<AddressEntity> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "address_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @Basic
    @Column(name = "address", length = 50, nullable = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String address;

    @Basic
    @Column(name = "address2", length = 50, nullable = true)
    @Size(min = 1, max = 50)
    private String address2;

    @Basic
    @Column(name = "district", length = 20, nullable = false)
    @NotNull
    @Size(min = 1, max = 20)
    private String district;

    @Basic
    @Column(name = "city_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false,
            insertable = false, updatable = false)
    @NotNull
    private Integer cityId;

    @Basic
    @Column(name = "postal_code", length = 10, nullable = true)
    @Size(min = 1, max = 10)
    private String postalCode;

    @Basic
    @Column(name = "phone", length = 20, nullable = false)
    @NotNull
    @Size(min = 1, max = 20)
    private String phone;

    // @Basic
    // @Column(name = "location", columnDefinition = "GEOMETRY SRID 0", nullable = false)
    // @Convert(converter = GeometryConverter.class)
    // @NotNull
    // private Point location;

    @Basic
    @Column(name = "last_update", columnDefinition = "TIMESTAMP", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @UpdateTimestamp
    @NotNull
    private LocalDateTime lastUpdate;

    @ManyToOne
    @JoinColumn(name = "city_id", referencedColumnName = "city_id", nullable = false)
    @NotNull
    @ToString.Exclude
    private CityEntity cityByCityId;

    @Override
    public void update(AddressEntity entity) {
        this.address = entity.address;
        this.address2 = entity.address2;
        this.district = entity.district;
        this.cityId = entity.cityId;
        this.postalCode = entity.postalCode;
        this.phone = entity.phone;
        // this.location = entity.location;
        this.lastUpdate = entity.lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AddressEntity that = (AddressEntity) o;
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
        // return Objects.hashCode(addressId, address, address2, district, cityId, postalCode, phone,
        //         location, lastUpdate);
        return Objects.hashCode(addressId, address, address2, district, cityId, postalCode, phone, lastUpdate);
    }
}
