package com.felipe.belo.mvp.application.user.mapper;

import com.felipe.belo.mvp.application.role.dto.RoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleListDto;
import com.felipe.belo.mvp.application.user.dto.CreateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UserEntityListDto;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.command.CreateUserCommand;
import com.felipe.belo.mvp.usecase.user.command.UpdateUserCommand;

public class UserDtoMapper {
    public CreateUserCommand toCreateCommand(CreateUserEntityDto dto) {
        return new CreateUserCommand(dto.name(), dto.email(), dto.password(), dto.role() == null ? null : dto.role().id());
    }

    public UpdateUserCommand toUpdateCommand(UpdateUserEntityDto dto) {
        return new UpdateUserCommand(dto.name(), dto.email(), null, dto.role() == null ? null : dto.role().id());
    }

    public UserEntityDto toDto(User user) {
        return new UserEntityDto(
                user.getCreatedBy(),
                user.getCreatedDate(),
                user.getLastModifiedBy(),
                user.getLastModifiedDate(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                toRoleDto(user.getRole()),
                user.getDeletedBy(),
                user.getDeletedAt()
        );
    }

    public UserEntityListDto toListDto(User user) {
        return new UserEntityListDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                toRoleListDto(user.getRole()),
                user.getDeletedBy(),
                user.getDeletedAt()
        );
    }

    private RoleDto toRoleDto(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleDto(
                role.getCreatedBy(),
                role.getCreatedDate(),
                role.getLastModifiedBy(),
                role.getLastModifiedDate(),
                role.getId(),
                role.getName(),
                role.getPermissions()
        );
    }

    private RoleListDto toRoleListDto(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleListDto(
                role.getId(),
                role.getName(),
                role.getPermissions(),
                role.getDeletedAt()
        );
    }
}
