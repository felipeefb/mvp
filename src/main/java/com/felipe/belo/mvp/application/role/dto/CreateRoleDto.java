package com.felipe.belo.mvp.application.role.dto;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.felipe.belo.mvp.core.domain.model.Role} creation.
 *
 * @param name        the role name
 * @param permissions the set of permissions associated with the role
 */
public record CreateRoleDto(
        @NotBlank(message = "{I18nConstants.MESSAGE_ROLE_NAME_REQUIRED}")
        @Size(min = 5, max = 20, message = "{I18nConstants.MESSAGE_ROLE_NAME_LENGTH}")
        String name, Set<Permissions> permissions) implements Serializable {
    @Serial
    private static final long serialVersionUID = 4582102258170273066L;
}