package com.felipe.belo.mvp.application.user.mapper;

import com.felipe.belo.mvp.application.shared.api.UserEmailResolver;
import com.felipe.belo.mvp.application.user.dto.CreateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UserEntityListDto;
import com.felipe.belo.mvp.application.user.dto.UserRoleDto;
import com.felipe.belo.mvp.application.user.dto.UserRoleListDto;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.command.CreateUserCommand;
import com.felipe.belo.mvp.usecase.user.command.UpdateUserCommand;
import org.springframework.stereotype.Component;

/**
 * Maps between user DTOs and domain models at the application boundary.
 */
@Component
public class UserDtoMapper {
    private final UserEmailResolver userEmailResolver;

    /**
     * Creates a mapper using the provided resolver.
     *
     * @param userEmailResolver resolver for user e-mails
     */
    public UserDtoMapper(UserEmailResolver userEmailResolver) {
        this.userEmailResolver = userEmailResolver;
    }

    /**
     * Maps a create DTO to a domain create command.
     *
     * @param dto inbound DTO
     * @return create command
     */
    public CreateUserCommand toCreateCommand(CreateUserEntityDto dto) {
        return new CreateUserCommand(dto.name(), dto.email(), dto.password(), dto.role() == null ? null : dto.role().id());
    }

    /**
     * Maps an update DTO to a domain update command.
     *
     * @param dto inbound DTO
     * @return update command
     */
    public UpdateUserCommand toUpdateCommand(UpdateUserEntityDto dto) {
        return new UpdateUserCommand(dto.name(), dto.email(), null, dto.role() == null ? null : dto.role().id());
    }

    /**
     * Maps a domain user to a create DTO.
     *
     * @param user domain model
     * @return create DTO
     */
    public CreateUserEntityDto toCreateDto(User user) {
        if (user == null) {
            return null;
        }
        return new CreateUserEntityDto(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                toRoleDto(user.getRole())
        );
    }

    /**
     * Maps a domain user to an update DTO.
     *
     * @param user domain model
     * @return update DTO
     */
    public UpdateUserEntityDto toUpdateDto(User user) {
        if (user == null) {
            return null;
        }
        return new UpdateUserEntityDto(
                user.getName(),
                user.getEmail(),
                toRoleDto(user.getRole())
        );
    }

    /**
     * Maps a domain user to a detailed DTO.
     *
     * @param user domain model
     * @return DTO
     */
    public UserEntityDto toDto(User user) {
        return new UserEntityDto(
                user.getCreatedBy(),
                user.getCreatedDate(),
                userEmailResolver.resolveUserEmail(user.getLastModifiedBy()),
                user.getLastModifiedDate(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                toRoleDto(user.getRole()),
                userEmailResolver.resolveUserEmail(user.getDeletedBy()),
                user.getDeletedAt()
        );
    }

    /**
     * Maps a domain user to a list DTO.
     *
     * @param user domain model
     * @return list DTO
     */
    public UserEntityListDto toListDto(User user) {
        return new UserEntityListDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                toRoleListDto(user.getRole()),
                userEmailResolver.resolveUserEmail(user.getDeletedBy()),
                user.getDeletedAt()
        );
    }

    private UserRoleDto toRoleDto(Role role) {
        if (role == null) {
            return null;
        }
        return new UserRoleDto(
                role.getCreatedBy(),
                role.getCreatedDate(),
                userEmailResolver.resolveUserEmail(role.getLastModifiedBy()),
                role.getLastModifiedDate(),
                role.getId(),
                role.getName(),
                role.getPermissions()
        );
    }

    private UserRoleListDto toRoleListDto(Role role) {
        if (role == null) {
            return null;
        }
        return new UserRoleListDto(
                role.getId(),
                role.getName(),
                role.getPermissions(),
                role.getDeletedAt()
        );
    }

}
