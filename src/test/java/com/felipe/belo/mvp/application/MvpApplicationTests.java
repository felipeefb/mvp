package com.felipe.belo.mvp.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assumptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        // Clear user-defined datasource so @ServiceConnection takes over
        "spring.datasource.url=",
        "spring.datasource.username=",
        "spring.datasource.password=",
        // activate dedicated test profile to avoid dev overrides
        "spring.profiles.active=test",
        // keep Flyway enabled to run migrations in the container
        "spring.flyway.enabled=true",
        // reduce noise
        "spring.jpa.show-sql=false"
})
class MvpApplicationTests {

    @BeforeAll
    static void verifyDockerAvailable() {
        Assumptions.assumeTrue(isDockerReachable(), "Docker daemon not reachable; skipping container-based test");
    }

    private static boolean isDockerReachable() {
        try {
            org.testcontainers.DockerClientFactory.instance().client();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ContainerConfig {
        @Bean
        @org.springframework.boot.testcontainers.service.connection.ServiceConnection
        org.testcontainers.containers.PostgreSQLContainer<?> postgresContainer() {
            return new org.testcontainers.containers.PostgreSQLContainer<>(
                    org.testcontainers.utility.DockerImageName.parse("postgres:latest")
            );
        }
    }

    @Test
    void contextLoads() {
    }

}