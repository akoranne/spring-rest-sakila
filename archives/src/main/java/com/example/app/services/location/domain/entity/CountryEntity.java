package com.example.app.services.location.domain.entity;

import com.example.app.common.constant.Country;
import com.example.app.common.domain.dto.Updatable;
import com.example.app.services.location.domain.converter.CountryConverter;
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

@Entity(name = "country")
@Table(name = "country", schema = "sakila")
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CountryEntity implements Serializable, Updatable<CountryEntity> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "country_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = CountryConverter.class)
    private Country countryId;

    @Basic
    @Column(name = "country", length = 50, nullable = false)
    @NotNull
    @Size(min = 1, max = 50)
    private String country;

    @Basic
    @Column(name = "last_update", columnDefinition = "TIMESTAMP", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @UpdateTimestamp
    @NotNull
    private LocalDateTime lastUpdate;

    @Override
    public void update(CountryEntity entity) {
        this.country = entity.country;
        this.lastUpdate = entity.lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CountryEntity that = (CountryEntity) o;
        return countryId == that.countryId
                && Objects.equal(country, that.country)
                && Objects.equal(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(countryId, country, lastUpdate);
    }
}
