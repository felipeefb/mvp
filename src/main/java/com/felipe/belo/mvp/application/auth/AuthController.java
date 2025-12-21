package com.felipe.belo.mvp.application.auth;

import com.felipe.belo.mvp.core.config.security.SecurityProps;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Controller responsible for handling authentication-related operations,
 * including login and token refresh functionalities. Exposes endpoints to
 * authenticate users and generate new JWT tokens.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    private final SecurityProps securityProps;

    /**
     * Creates a new authentication controller.
     *
     * @param userRepository repository for user lookup
     * @param passwordEncoder encoder for password verification
     * @param jwtEncoder encoder for generating JWTs
     * @param jwtDecoder decoder for validating JWTs
     * @param securityProps security configuration properties
     */
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, SecurityProps securityProps) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.securityProps = securityProps;
    }

    /**
     * Login request payload.
     *
     * @param email    user e-mail
     * @param password raw password
     */
    public static record LoginRequest(String email, String password) {}

    /**
     * Token response payload containing access and refresh tokens.
     *
     * @param accessToken  short-lived access token
     * @param refreshToken long-lived refresh token
     */
    public static record TokenResponse(String accessToken, String refreshToken) {}

    /**
     * Refresh request payload.
     *
     * @param refreshToken an existing valid refresh token
     */
    public static record RefreshRequest(String refreshToken) {}

    /**
     * Authenticates a user and returns JWT tokens.
     *
     * @param loginReq login request
     * @return a pair of access and refresh tokens
     */
    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Validated LoginRequest loginReq) {

        User user = userRepository.findByEmailIgnoreCase(loginReq.email())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED,
                        "user.invalid.credentials"));

        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "user.invalid.credentials");
        }

        String accessToken = generateToken(user, securityProps.accessTokenExpirationMinutes());
        String refreshToken = generateToken(user, securityProps.refreshTokenExpirationMinutes());
        return new TokenResponse(accessToken, refreshToken);
    }


    /**
     * Validates a refresh token and issues a new token pair.
     *
     * @param refreshReq refresh request
     * @return new access and refresh tokens
     */
    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestBody @Validated RefreshRequest refreshReq) {
        String refreshToken = refreshReq.refreshToken();
        try {
            jwtDecoder.decode(refreshToken);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "access.denied");
        }

        String userEmail = jwtDecoder.decode(refreshToken).getSubject();
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "user.not.found"));

        String newAccessToken = generateToken(user, securityProps.accessTokenExpirationMinutes());
        String newRefreshToken = generateToken(user, securityProps.refreshTokenExpirationMinutes());
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    /**
     * Generates a signed JWT token for the given user.
     *
     * @param user             the subject user
     * @param expiresInMinutes expiration window in minutes
     * @return a signed JWT string
     */
    private String generateToken(User user, int expiresInMinutes) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expiresInMinutes, ChronoUnit.MINUTES);
        
        // Create JWS Header with explicit algorithm
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        
        // Monta as claims do JWT
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
