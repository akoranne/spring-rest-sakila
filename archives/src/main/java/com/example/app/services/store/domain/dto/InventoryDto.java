package com.example.app.services.store.domain.dto;

import com.example.app.services.catalog.domain.dto.FilmDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

public class InventoryDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Inventory {
        @JsonProperty(Fields.inventoryId)
        private Integer inventoryId;

        @JsonProperty(Fields.filmId)
        private Integer filmId;

        @JsonProperty(Fields.storeId)
        private Integer storeId;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @JsonIgnore
        @JsonProperty(Fields.filmByFilmId)
        @ToString.Exclude
        private FilmDto.Film filmByFilmId;

        @JsonIgnore
        @JsonProperty(Fields.storeByStoreId)
        @ToString.Exclude
        private StoreDto.Store storeByStoreId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Inventory that = (Inventory) o;
            return Objects.equal(inventoryId, that.inventoryId)
                    && Objects.equal(filmId, that.filmId)
                    && Objects.equal(storeId, that.storeId)
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(inventoryId, filmId, storeId, lastUpdate);
        }
    }
}
