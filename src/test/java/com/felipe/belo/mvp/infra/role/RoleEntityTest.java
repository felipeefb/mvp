package com.felipe.belo.mvp.infra.role;

import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RoleEntityTest {

    @Test
    void gettersAndSettersWork() {
        RoleEntity role = new RoleEntity();
        role.setName("Managers");
        Set<Permissions> perms = EnumSet.of(Permissions.ROLE_READ, Permissions.ROLE_LIST);
        role.setPermissions(perms);
        UUID deleter = UUID.randomUUID();
        role.setDeletedBy(deleter);
        LocalDateTime now = LocalDateTime.now();
        role.setDeletedAt(now);

        assertThat(role.getName()).isEqualTo("Managers");
        assertThat(role.getPermissions()).containsExactlyInAnyOrder(Permissions.ROLE_READ, Permissions.ROLE_LIST);
        assertThat(role.getDeletedBy()).isEqualTo(deleter);
        assertThat(role.getDeletedAt()).isEqualTo(now);
    }
}
