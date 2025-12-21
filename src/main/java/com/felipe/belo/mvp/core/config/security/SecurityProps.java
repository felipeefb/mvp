package com.felipe.belo.mvp.core.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security properties for JWT.
 * <p>
 * Values are read from application properties using the prefix {@code security.jwt}.
 * For example: {@code security.jwt.secret=...}
 *
 * @param secret the symmetric signing secret used for HS256 JWTs
 * @param accessTokenExpirationMinutes access token expiration time in minutes
 * @param refreshTokenExpirationMinutes refresh token expiration time in minutes
 */
@ConfigurationProperties(prefix = "security.jwt")
public record SecurityProps(
        String secret,
        int accessTokenExpirationMinutes,
        int refreshTokenExpirationMinutes
) {
}