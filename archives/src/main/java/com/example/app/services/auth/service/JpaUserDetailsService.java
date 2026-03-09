package com.example.app.services.auth.service;

import com.example.app.services.auth.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var user = authorityRepository.findAuthorityEntityByEmail(username);
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getAuthority())
                .build();
    }

    @Transactional(readOnly = true)
    public UserDetails findUser(String email, String password) {
        final var user = loadUserByUsername(email);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        throw new IllegalArgumentException("Invalid password");
    }
}
