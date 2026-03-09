package com.example.app.services.auth.controller;

import com.example.app.common.security.JwtTokenProvider;
import com.example.app.services.auth.domain.dto.AuthorityDto;
import com.example.app.services.auth.service.JpaUserDetailsService;
import com.example.app.services.auth.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/login")
@RequiredArgsConstructor
public class LoginController {
    private final JpaUserDetailsService userDetailsService;
    private final SecurityService securityService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping(path = "")
    public ResponseEntity<Void> userLogin(@RequestBody AuthorityDto.Login login) {
        final var user = userDetailsService.findUser(login.getEmail(), login.getPassword());
        final var jwt = jwtTokenProvider.createToken(user);
        securityService.authenticateToken(user, jwt);

        final var headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        return ResponseEntity.ok().headers(headers).build();
    }
}
