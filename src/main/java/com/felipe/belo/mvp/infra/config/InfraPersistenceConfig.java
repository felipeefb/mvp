package com.felipe.belo.mvp.infra.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Infrastructure persistence configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.felipe.belo.mvp.infra")
@EntityScan(basePackages = {
        "com.felipe.belo.mvp.infra",
        "org.springframework.modulith.events.jpa"
})
public class InfraPersistenceConfig {
}
