package com.felipe.belo.mvp.role;

import com.felipe.belo.mvp.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.role.dto.RoleDto;
import com.felipe.belo.mvp.role.dto.RoleListDto;
import com.felipe.belo.mvp.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.role.entity.Role;
import com.felipe.belo.mvp.role.mapper.RoleMapper;
import com.felipe.belo.mvp.utils.permissions.Permissions;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest {

    private final RoleMapper mapper = new com.felipe.belo.mvp.role.mapper.RoleMapperImpl();

    @Test
    void toEntity_FromCreateRoleDto() {
        CreateRoleDto dto = new CreateRoleDto("Managers", EnumSet.of(Permissions.ROLE_CREATE, Permissions.ROLE_LIST));
        Role entity = mapper.toEntity(dto);
        assertThat(entity.getName()).isEqualTo("Managers");
        assertThat(entity.getPermissions()).containsExactlyInAnyOrder(Permissions.ROLE_CREATE, Permissions.ROLE_LIST);
    }

    @Test
    void toDto_FromEntity() {
        Role entity = new Role();
        entity.setName("Admins");
        entity.setPermissions(EnumSet.of(Permissions.ROLE_READ, Permissions.ROLE_UPDATE));
        RoleDto dto = mapper.toDto(entity);
        assertThat(dto.name()).isEqualTo("Admins");
        assertThat(dto.permissions()).containsExactlyInAnyOrder(Permissions.ROLE_READ, Permissions.ROLE_UPDATE);
    }

    @Test
    void partialUpdate_IgnoresNulls() {
        Role entity = new Role();
        entity.setName("Old");
        entity.setPermissions(EnumSet.of(Permissions.ROLE_LIST));

        UpdateRoleDto update = new UpdateRoleDto(null, EnumSet.of(Permissions.ROLE_CREATE));
        Role updated = mapper.partialUpdate(update, entity);

        assertThat(updated.getName()).isEqualTo("Old");
        assertThat(updated.getPermissions()).containsExactlyInAnyOrder(Permissions.ROLE_CREATE);
    }

    @Test
    void toDtoList_FromIterable() {
        Role r1 = new Role();
        r1.setDeletedAt(null);
        r1.setName("A");
        Role r2 = new Role();
        r2.setName("B");
        List<RoleListDto> list = mapper.toDtoList(List.of(r1, r2));
        assertThat(list).hasSize(2);
        assertThat(list.stream().map(RoleListDto::name)).containsExactlyInAnyOrder("A", "B");
    }
}