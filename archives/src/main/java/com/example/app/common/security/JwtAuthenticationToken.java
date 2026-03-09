package com.example.app.common.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String accessToken;
    private final String email;

    public JwtAuthenticationToken(String accessToken, String email, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        this.accessToken = accessToken;
        this.email = email;
    }

    public JwtAuthenticationToken(String substring) {
        super(null);
        this.accessToken = substring;
        this.email = null;
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }
}
