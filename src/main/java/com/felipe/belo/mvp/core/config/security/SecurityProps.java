package com.felipe.belo.mvp.core.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Config de JWT lida a partir de:
 * security.jwt.secret=...
 */
@ConfigurationProperties(prefix = "security.jwt")
public record SecurityProps(String secret) {
}