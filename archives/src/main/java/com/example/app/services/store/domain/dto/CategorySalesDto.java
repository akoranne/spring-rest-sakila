package com.example.app.services.store.domain.dto;

import com.example.app.common.constant.Category;
import com.example.app.services.catalog.domain.converter.CategoryConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Objects;
import jakarta.persistence.Convert;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class CategorySalesDto {
    /**
     * The sales by category model provides the data for the total sales by film category.
     */
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySales {
        /**
         * category
         */
        @JsonProperty(Fields.category)
        @Convert(converter = CategoryConverter.class)
        private Category category;

        /**
         * total sales
         */
        @JsonProperty(Fields.totalSales)
        private BigDecimal totalSales;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final CategorySales that = (CategorySales) o;
            return category == that.category
                    && Objects.equal(totalSales, that.totalSales);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(category, totalSales);
        }
    }

    @Relation(collectionRelation = "categorySalesList", itemRelation = "categorySales")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorySalesResponse extends RepresentationModel<CategorySalesResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private CategorySales categorySales;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final CategorySalesResponse that = (CategorySalesResponse) o;
            return Objects.equal(categorySales, that.categorySales);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), categorySales);
        }
    }
}
