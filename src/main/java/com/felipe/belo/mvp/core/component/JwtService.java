package com.felipe.belo.mvp.core.component;

import com.felipe.belo.mvp.core.config.security.SecurityProps;
import com.felipe.belo.mvp.core.domain.model.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Service for JWT operations including generation and decoding.
 */
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final SecurityProps securityProps;

    /**
     * Creates a new JwtService.
     *
     * @param jwtEncoder    the JWT encoder
     * @param jwtDecoder    the JWT decoder
     * @param securityProps the security properties
     */
    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, SecurityProps securityProps) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.securityProps = securityProps;
    }

    /**
     * Generates an access token for the given user.
     *
     * @param user the user
     * @return the generated access token
     */
    public String generateAccessToken(User user) {
        return generateToken(user, securityProps.accessTokenExpirationMinutes());
    }

    /**
     * Generates a refresh token for the given user.
     *
     * @param user the user
     * @return the generated refresh token
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, securityProps.refreshTokenExpirationMinutes());
    }

    /**
     * Decodes the given token.
     *
     * @param token the token to decode
     * @return the decoded JWT
     * @throws JwtException if the token is invalid
     */
    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }

    private String generateToken(User user, int expiresInMinutes) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expiresInMinutes, ChronoUnit.MINUTES);

        // Create JWS Header with explicit algorithm
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        // Build JWT claims
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(expiry)
                .claim("uid", user.getId().toString())
                .claim("role", user.getRole().getName())
                .claim("permissions", user.getRole().getPermissions())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}