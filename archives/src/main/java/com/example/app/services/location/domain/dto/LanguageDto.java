package com.example.app.services.location.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

public class LanguageDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Language {
        @JsonProperty(Fields.languageId)
        private com.example.app.common.constant.Language languageId;

        @JsonProperty(Fields.name)
        @Size(min = 1, max = 20)
        private String name;

        @JsonProperty(Fields.lastUpdate)
        private LocalDateTime lastUpdate;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Language that = (Language) o;
            return languageId == that.languageId
                    && Objects.equal(name, that.name)
                    && Objects.equal(lastUpdate, that.lastUpdate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(languageId, name, lastUpdate);
        }
    }
}
