package com.example.app.services.auth.domain.entity;

import com.example.app.services.auth.domain.converter.UserRoleConverter;
import com.example.app.services.auth.domain.vo.UserRole;
import com.google.common.base.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumSet;

@Entity(name = "authority")
@Table(name = "authority", schema = "sakila")
@Getter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AuthorityEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "authority_id", columnDefinition = "SMALLINT UNSIGNED", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authorityId;

    @Basic
    @Column(name = "email", length = 50, nullable = false, unique = true)
    @Email
    @NotNull
    @Size(min = 1, max = 50)
    private String email;

    @Basic
    @Column(name = "password", columnDefinition = "VARCHAR(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin", nullable = false)
    @NotNull
    private String password;

    @Basic
    @Column(name = "authority", columnDefinition = "SET('ROLE_READ', 'ROLE_WRITE', 'ROLE_MANAGE', 'ROLE_ADMIN')",
            nullable = false)
    @Convert(converter = UserRoleConverter.class)
    private EnumSet<UserRole> authority;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AuthorityEntity that = (AuthorityEntity) o;
        return Objects.equal(authorityId, that.authorityId)
                && Objects.equal(email, that.email)
                && Objects.equal(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authorityId, email, authority);
    }
}
