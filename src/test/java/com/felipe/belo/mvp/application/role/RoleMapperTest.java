package com.felipe.belo.mvp.application.role;

import com.felipe.belo.mvp.application.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleListDto;
import com.felipe.belo.mvp.application.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.application.role.mapper.RoleDtoMapper;
import com.felipe.belo.mvp.application.shared.api.UserEmailResolver;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.role.command.CreateRoleCommand;
import com.felipe.belo.mvp.usecase.role.command.UpdateRoleCommand;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import com.felipe.belo.mvp.usecase.user.LookupUserByIdUseCase;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest {

    private final RoleDtoMapper mapper = new RoleDtoMapper(new UserEmailResolver(new RoleMapperUserLookupStub()));

    @Test
    void toCreateCommand_FromCreateRoleDto() {
        CreateRoleDto dto = new CreateRoleDto("Managers", EnumSet.of(Permissions.ROLE_CREATE, Permissions.ROLE_LIST));
        CreateRoleCommand command = mapper.toCreateCommand(dto);
        assertThat(command.name()).isEqualTo("Managers");
        assertThat(command.permissions()).containsExactlyInAnyOrder(Permissions.ROLE_CREATE, Permissions.ROLE_LIST);
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
    void toUpdateCommand_AllowsNulls() {
        UpdateRoleDto update = new UpdateRoleDto(null, EnumSet.of(Permissions.ROLE_CREATE));
        UpdateRoleCommand command = mapper.toUpdateCommand(update);
        assertThat(command.name()).isNull();
        assertThat(command.permissions()).containsExactlyInAnyOrder(Permissions.ROLE_CREATE);
    }

    @Test
    void toDtoList_FromIterable() {
        Role r1 = new Role();
        r1.setDeletedAt(null);
        r1.setName("A");
        Role r2 = new Role();
        r2.setName("B");
        List<RoleListDto> list = mapper.toListDtos(List.of(r1, r2));
        assertThat(list).hasSize(2);
        assertThat(list.stream().map(RoleListDto::name)).containsExactlyInAnyOrder("A", "B");
    }
}

class RoleMapperUserLookupStub implements LookupUserByIdUseCase {
    @Override
    public java.util.Optional<User> findOptionalById(java.util.UUID id) {
        return java.util.Optional.empty();
    }
}
