package com.felipe.belo.mvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.felipe.belo.mvp.core.config.security.SecurityProps;

/**
 * Spring Boot application entry point.
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties({SecurityProps.class})
public class MvpApplication {

    /**
     * Utility constructor to prevent instantiation.
     */
    public MvpApplication() {
    }

    /**
     * Bootstraps the Spring application.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MvpApplication.class, args);
    }

}