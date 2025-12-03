package com.felipe.belo.mvp.role;
import com.felipe.belo.mvp.role.entity.Role;
import com.felipe.belo.mvp.role.repository.RoleRepository;
import com.felipe.belo.mvp.utils.permissions.Permissions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.flyway.enabled=true",
        "spring.jpa.show-sql=false"
})
class RoleRepositoryIT {

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

    @Autowired
    private RoleRepository roleRepository;

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
    void saveAndFindAllByDeletedAtIsNull() {
        Role r = new Role();
        r.setName("QA Team");
        r.setPermissions(EnumSet.of(Permissions.ROLE_LIST));
        roleRepository.save(r);

        List<Role> roles = roleRepository.findAllByDeletedAtIsNull();
        assertThat(roles.stream().anyMatch(x -> "QA Team".equals(x.getName()))).isTrue();
    }

    @Test
    void findByNormalizedName_IgnoresSpacesAndCase() {
        Role r = new Role();
        r.setName("  qa  team  ");
        r.setPermissions(EnumSet.of(Permissions.ROLE_LIST));
        roleRepository.save(r);

        Optional<Role> found = roleRepository.findByNormalizedName("QA   TEAM");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("  qa  team  ");
    }
}