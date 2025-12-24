package com.felipe.belo.mvp.application.role.mapper;

import com.felipe.belo.mvp.application.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleListDto;
import com.felipe.belo.mvp.application.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.application.shared.api.UserEmailResolver;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.usecase.role.command.CreateRoleCommand;
import com.felipe.belo.mvp.usecase.role.command.UpdateRoleCommand;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps between role DTOs and domain models at the application boundary.
 */
@Component
public class RoleDtoMapper {
    private final UserEmailResolver userEmailResolver;

    /**
     * Creates a mapper using the provided user resolver.
     *
     * @param userEmailResolver resolver for user emails
     */
    public RoleDtoMapper(UserEmailResolver userEmailResolver) {
        this.userEmailResolver = userEmailResolver;
    }

    /**
     * Converts a create DTO to a create command.
     *
     * @param dto inbound DTO
     * @return create command
     */
    public CreateRoleCommand toCreateCommand(CreateRoleDto dto) {
        return new CreateRoleCommand(dto.name(), dto.permissions());
    }

    /**
     * Converts an update DTO to an update command.
     *
     * @param dto inbound DTO
     * @return update command
     */
    public UpdateRoleCommand toUpdateCommand(UpdateRoleDto dto) {
        return new UpdateRoleCommand(dto.name(), dto.permissions());
    }

    /**
     * Maps a domain role to a create DTO.
     *
     * @param role domain model
     * @return create DTO
     */
    public CreateRoleDto toCreateDto(Role role) {
        if (role == null) {
            return null;
        }
        return new CreateRoleDto(role.getName(), role.getPermissions());
    }

    /**
     * Maps a domain role to an update DTO.
     *
     * @param role domain model
     * @return update DTO
     */
    public UpdateRoleDto toUpdateDto(Role role) {
        if (role == null) {
            return null;
        }
        return new UpdateRoleDto(role.getName(), role.getPermissions());
    }

    /**
     * Maps a domain role to a detailed DTO.
     *
     * @param role domain model
     * @return DTO
     */
    public RoleDto toDto(Role role) {
        return new RoleDto(
                role.getCreatedBy(),
                role.getCreatedDate(),
                userEmailResolver.resolveUserEmail(role.getLastModifiedBy()),
                role.getLastModifiedDate(),
                role.getId(),
                role.getName(),
                role.getPermissions()
        );
    }

    /**
     * Maps a domain role to a list DTO.
     *
     * @param role domain model
     * @return list DTO
     */
    public RoleListDto toListDto(Role role) {
        return new RoleListDto(
                role.getId(),
                role.getName(),
                role.getPermissions(),
                role.getDeletedAt()
        );
    }

    /**
     * Maps a list of domain roles to list DTOs.
     *
     * @param roles domain roles
     * @return list DTOs
     */
    public List<RoleListDto> toListDtos(List<Role> roles) {
        return roles.stream().map(this::toListDto).toList();
    }

}
