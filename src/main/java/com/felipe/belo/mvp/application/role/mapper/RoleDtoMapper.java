package com.felipe.belo.mvp.application.role.mapper;

import com.felipe.belo.mvp.application.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleListDto;
import com.felipe.belo.mvp.application.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.usecase.role.command.CreateRoleCommand;
import com.felipe.belo.mvp.usecase.role.command.UpdateRoleCommand;

import java.util.List;

public class RoleDtoMapper {
    public CreateRoleCommand toCreateCommand(CreateRoleDto dto) {
        return new CreateRoleCommand(dto.name(), dto.permissions());
    }

    public UpdateRoleCommand toUpdateCommand(UpdateRoleDto dto) {
        return new UpdateRoleCommand(dto.name(), dto.permissions());
    }

    public RoleDto toDto(Role role) {
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

    public RoleListDto toListDto(Role role) {
        return new RoleListDto(
                role.getId(),
                role.getName(),
                role.getPermissions(),
                role.getDeletedAt()
        );
    }

    public List<RoleListDto> toListDtos(List<Role> roles) {
        return roles.stream().map(this::toListDto).toList();
    }
}
