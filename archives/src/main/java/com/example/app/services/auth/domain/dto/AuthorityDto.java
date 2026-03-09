package com.example.app.services.auth.domain.dto;

import com.example.app.services.auth.domain.converter.UserRoleConverter;
import com.example.app.services.auth.domain.vo.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.util.Set;

public class AuthorityDto {
    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Authority {
        @JsonProperty(Fields.authorityId)
        private Integer authorityId;

        @JsonProperty(Fields.email)
        @Email
        @Size(min = 1, max = 50)
        private String email;

        @JsonProperty(Fields.password)
        private String password;

        @JsonProperty(Fields.authority)
        @Convert(converter = UserRoleConverter.class)
        private Set<UserRole> authority;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Authority authority = (Authority) o;
            return Objects.equal(authorityId, authority.authorityId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(authorityId);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        @JsonProperty(Fields.email)
        @Email
        @Size(min = 1, max = 50)
        private String email;

        @JsonProperty(Fields.password)
        private String password;

        @JsonProperty(Fields.authority)
        @Convert(converter = UserRoleConverter.class)
        private Set<UserRole> authority;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final User user = (User) o;
            return Objects.equal(email, user.email);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(email);
        }
    }

    @Getter
    @Setter
    @ToString
    @FieldNameConstants
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        @Email
        @Size(min = 1, max = 50)
        private String email;
        private String password;
    }
}
