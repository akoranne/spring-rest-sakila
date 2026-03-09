package com.example.app.services.auth.service;

import com.example.app.common.security.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final AuthenticationManager authenticationManager;

    public void authenticateToken(UserDetails user, String jwt) {
        final var token = new JwtAuthenticationToken(jwt, user.getUsername(), user.getAuthorities());
        authenticationManager.authenticate(token);
    }
}
