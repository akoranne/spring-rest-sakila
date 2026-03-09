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

public class StoreDetailsDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreDetails {
        @JsonProperty(Fields.id)
        private Integer id;

        @JsonProperty(Fields.store)
        private String store;

        @JsonProperty(Fields.manager)
        private String manager;

        @JsonProperty(Fields.address)
        private String address;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final StoreDetails that = (StoreDetails) o;
            return Objects.equal(id, that.id)
                    && Objects.equal(store, that.store)
                    && Objects.equal(manager, that.manager)
                    && Objects.equal(address, that.address);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id, store, manager, address);
        }
    }

    @Relation(collectionRelation = "storeDetailsList", itemRelation = "storeDetails")
    @Getter
    @Setter
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreDetailsResponse extends RepresentationModel<StoreDetailsResponse> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        @JsonUnwrapped
        private StoreDetails storeDetails;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            final StoreDetailsResponse that = (StoreDetailsResponse) o;
            return Objects.equal(storeDetails, that.storeDetails);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), storeDetails);
        }
    }
}
