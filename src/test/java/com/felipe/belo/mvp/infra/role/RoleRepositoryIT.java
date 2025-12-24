package com.felipe.belo.mvp.infra.role;
import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import com.felipe.belo.mvp.infra.role.repository.RoleJpaRepository;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assumptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.felipe.belo.mvp.MvpApplication.class)
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
    private RoleJpaRepository roleRepository;

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
        RoleEntity r = new RoleEntity();
        r.setName("QA Team");
        r.setPermissions(EnumSet.of(Permissions.ROLE_LIST));
        roleRepository.save(r);

        List<RoleEntity> roles = roleRepository.findAllByDeletedAtIsNull();
        assertThat(roles.stream().anyMatch(x -> "QA Team".equals(x.getName()))).isTrue();
    }

    @Test
    void findByNormalizedName_IgnoresSpacesAndCase() {
        RoleEntity r = new RoleEntity();
        r.setName("  qa  team  ");
        r.setPermissions(EnumSet.of(Permissions.ROLE_LIST));
        roleRepository.save(r);

        Optional<RoleEntity> found = roleRepository.findByNormalizedName("QA   TEAM");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("  qa  team  ");
    }
}
