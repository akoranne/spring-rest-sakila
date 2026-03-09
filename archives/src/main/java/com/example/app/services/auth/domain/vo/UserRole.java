package com.example.app.services.auth.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum UserRole implements GrantedAuthority {
    ROLE_READ(Constants.ROLE_READ),
    ROLE_WRITE(Constants.ROLE_WRITE),
    ROLE_MANAGE(Constants.ROLE_MANAGE),
    ROLE_ADMIN(Constants.ROLE_ADMIN);

    public static final Map<String, UserRole> AUTHORITY_MAP = Stream.of(UserRole.values())
            .collect(Collectors.toUnmodifiableMap(UserRole::getAuthority, Function.identity()));

    private final String authority;

    public static class Constants {
        public static final String ROLE_READ = "ROLE_READ";
        public static final String ROLE_WRITE = "ROLE_WRITE";
        public static final String ROLE_MANAGE = "ROLE_MANAGE";
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
    }
}
