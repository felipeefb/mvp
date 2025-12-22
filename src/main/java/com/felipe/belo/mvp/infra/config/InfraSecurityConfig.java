package com.felipe.belo.mvp.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Infrastructure security beans.
 */
@Configuration
public class InfraSecurityConfig {
    /** Default constructor for component scanning. */
    public InfraSecurityConfig() { }

    /**
     * Provides a BCrypt password encoder bean.
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
