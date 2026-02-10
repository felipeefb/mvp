package com.felipe.belo.mvp.application.auth;

import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

class JwtTestHelpers {
    static Jwt jwtWithSubject(String subject) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subject);
        claims.put("exp", Instant.now().plusSeconds(60).getEpochSecond());
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), headers, claims);
    }

    static Jwt jwtWithRole(String token, String role) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "user@test.com");
        claims.put("role", role);
        claims.put("permissions", java.util.List.of("USER_READ"));
        claims.put("exp", Instant.now().plusSeconds(60).getEpochSecond());
        return new Jwt(token, Instant.now(), Instant.now().plusSeconds(60), headers, claims);
    }
}
