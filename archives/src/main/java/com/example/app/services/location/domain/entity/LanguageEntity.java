package com.example.app.services.location.domain.entity;

import com.example.app.common.constant.Language;
import com.example.app.common.domain.dto.Updatable;
import com.example.app.services.location.domain.converter.LanguageConverter;
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

@Entity(name = "language")
@Table(name = "language", schema = "sakila")
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LanguageEntity implements Serializable, Updatable<LanguageEntity> {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "language_id", columnDefinition = "TINYINT UNSIGNED", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Convert(converter = LanguageConverter.class)
    private Language languageId;

    @Basic
    @Column(name = "name", columnDefinition = "CHAR(20)", length = 20, nullable = false)
    @NotNull
    @Size(min = 1, max = 20)
    private String name;

    @Basic
    @Column(name = "last_update", columnDefinition = "TIMESTAMP", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @UpdateTimestamp
    @NotNull
    private LocalDateTime lastUpdate;

    @Override
    public void update(LanguageEntity entity) {
        this.name = entity.name;
        this.lastUpdate = entity.lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LanguageEntity that = (LanguageEntity) o;
        return languageId == that.languageId
                && Objects.equal(name, that.name)
                && Objects.equal(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(languageId, name, lastUpdate);
    }
}
