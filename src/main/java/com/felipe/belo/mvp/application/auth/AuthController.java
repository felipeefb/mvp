package com.felipe.belo.mvp.application.auth;

import com.felipe.belo.mvp.core.component.JwtService;
import com.felipe.belo.mvp.core.config.security.SecurityProps;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling authentication-related operations,
 * including login and token refresh functionalities. Exposes endpoints to
 * authenticate users and generate new JWT tokens.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "Auth", description = I18nConstants.SWAGGER_AUTH_TAG)
public class AuthController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    /**
     * Creates a new authentication controller.
     *
     * @param userRepository  repository for user lookup
     * @param passwordEncoder encoder for password verification
     * @param jwtService     service for JWT operations
     */
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
    @Operation(
            summary = I18nConstants.SWAGGER_AUTH_LOGIN_SUMMARY,
            description = I18nConstants.SWAGGER_AUTH_LOGIN_DESC
    )
    public TokenResponse login(@RequestBody @Validated LoginRequest loginReq) {

        User user = userRepository.findByEmailIgnoreCase(loginReq.email())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED,
                        "user.invalid.credentials"));

        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "user.invalid.credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new TokenResponse(accessToken, refreshToken);
    }


    /**
     * Validates a refresh token and issues a new token pair.
     *
     * @param refreshReq refresh request
     * @return new access and refresh tokens
     */
    @PostMapping("/refresh")
    @Operation(
            summary = I18nConstants.SWAGGER_AUTH_REFRESH_SUMMARY,
            description = I18nConstants.SWAGGER_AUTH_REFRESH_DESC
    )
    public TokenResponse refreshToken(@RequestBody @Validated RefreshRequest refreshReq) {
        String refreshToken = refreshReq.refreshToken();
        try {
            jwtService.decode(refreshToken);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "access.denied");
        }

        String userEmail = jwtService.decode(refreshToken).getSubject();
        User user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "user.not.found"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}