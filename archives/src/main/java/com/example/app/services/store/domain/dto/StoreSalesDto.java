package com.example.app.services.store.domain.dto;

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

public class StoreSalesDto {
    /**
     * The sales by store model provides the data for the total sales by store.
     */
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreSales {
        /**
         * store
         */
        @JsonProperty(Fields.store)
        private String store;

        /**
         * store manager
         */
        @JsonProperty(Fields.manager)
        private String manager;

        /**
         * total sales
         */
        @JsonProperty(Fields.totalSales)
        private BigDecimal totalSales;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final StoreSales that = (StoreSales) o;
            return Objects.equal(store, that.store)
                    && Objects.equal(manager, that.manager)
                    && Objects.equal(totalSales, that.totalSales);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(store, manager, totalSales);
        }
    }

    @Relation(collectionRelation = "storeSalesList", itemRelation = "storeSales")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreSalesResponse extends RepresentationModel<StoreSalesResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private StoreSales storeSales;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final StoreSalesResponse that = (StoreSalesResponse) o;
            return Objects.equal(storeSales, that.storeSales);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), storeSales);
        }
    }
}
