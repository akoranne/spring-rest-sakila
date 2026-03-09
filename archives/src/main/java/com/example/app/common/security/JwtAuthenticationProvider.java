package com.example.app.common.security;

import com.example.app.services.auth.domain.vo.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final var authToken = (JwtAuthenticationToken) authentication;
        final var accessToken = authToken.getAccessToken();
        final var claims = jwtTokenProvider.decodeToken(accessToken);
        if (claims == null) {
            return null;
        }

        final var email = claims.get("email", String.class);
        final var authorities = claims.get("authorities", List.class);
        final var authoritiesList = new ArrayList<GrantedAuthority>();
        for (var authority : authorities) {
            authoritiesList.add(UserRole.valueOf((String) authority));
        }
        final var token = new JwtAuthenticationToken(accessToken, email, authoritiesList);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }
}
