package com.felipe.belo.mvp.application.user;

import com.felipe.belo.mvp.application.shared.api.UserEmailResolver;
import com.felipe.belo.mvp.application.user.dto.CreateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UserEntityListDto;
import com.felipe.belo.mvp.application.user.dto.UserRoleDto;
import com.felipe.belo.mvp.application.user.mapper.UserDtoMapper;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import com.felipe.belo.mvp.usecase.user.command.CreateUserCommand;
import com.felipe.belo.mvp.usecase.user.command.UpdateUserCommand;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoMapperTest {

    private final UserEmailResolver resolver = new UserEmailResolver(id -> Optional.of(buildUser("lookup@test.com")));
    private final UserDtoMapper mapper = new UserDtoMapper(resolver);

    @Test
    void toCreateCommand_mapsAllFields() {
        UUID roleId = UUID.randomUUID();
        CreateUserCommand cmd = mapper.toCreateCommand(new CreateUserEntityDto("n", "e", "p", new UserRoleDto(null, null, null, null, roleId, "R", EnumSet.of(Permissions.USER_LIST))));
        assertThat(cmd.name()).isEqualTo("n");
        assertThat(cmd.email()).isEqualTo("e");
        assertThat(cmd.password()).isEqualTo("p");
        assertThat(cmd.roleId()).isEqualTo(roleId);
    }

    @Test
    void toUpdateCommand_mapsAllFields() {
        UUID roleId = UUID.randomUUID();
        UpdateUserCommand cmd = mapper.toUpdateCommand(new UpdateUserEntityDto("n", "e", new UserRoleDto(null, null, null, null, roleId, "R", EnumSet.of(Permissions.USER_LIST))));
        assertThat(cmd.name()).isEqualTo("n");
        assertThat(cmd.email()).isEqualTo("e");
        assertThat(cmd.roleId()).isEqualTo(roleId);
    }

    @Test
    void toDto_resolvesEmailsAndRole() {
        Role role = new Role(UUID.randomUUID(), "R", EnumSet.of(Permissions.USER_LIST));
        role.setCreatedBy(UUID.randomUUID());
        role.setLastModifiedBy(UUID.randomUUID());
        User user = buildUser("u@test.com");
        user.setRole(role);
        user.setCreatedBy(UUID.randomUUID());
        user.setLastModifiedBy(UUID.randomUUID());
        user.setDeletedBy(UUID.randomUUID());
        user.setDeletedAt(LocalDateTime.now());

        UserEntityDto dto = mapper.toDto(user);
        assertThat(dto.email()).isEqualTo("u@test.com");
        assertThat(dto.createdBy()).isEqualTo(user.getCreatedBy());
        assertThat(dto.deletedBy()).isEqualTo("lookup@test.com");
        assertThat(dto.role().permissions()).containsExactly(Permissions.USER_LIST);
    }

    @Test
    void toListDto_mapsMinimalFields() {
        Role role = new Role(UUID.randomUUID(), "R", EnumSet.of(Permissions.USER_LIST));
        User user = buildUser("u@test.com");
        user.setRole(role);
        user.setDeletedBy(UUID.randomUUID());
        user.setDeletedAt(LocalDateTime.now());

        UserEntityListDto dto = mapper.toListDto(user);
        assertThat(dto.id()).isEqualTo(user.getId());
        assertThat(dto.role().name()).isEqualTo("R");
    }

    private static User buildUser(String email) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setName("User");
        u.setEmail(email);
        u.setPassword("p");
        return u;
    }
}
