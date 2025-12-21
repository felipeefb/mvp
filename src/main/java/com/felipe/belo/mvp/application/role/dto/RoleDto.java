package com.felipe.belo.mvp.application.role.dto;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link com.felipe.belo.mvp.core.domain.model.Role}.
 *
 * @param createdBy        the user id who created the role
 * @param createdDate      the timestamp when the role was created
 * @param lastModifiedBy   the user id who last modified the role
 * @param lastModifiedDate the timestamp when the role was last modified
 * @param id               the role id
 * @param name             the role name
 * @param permissions      the set of permissions associated with the role
 */
public record RoleDto(UUID createdBy, LocalDateTime createdDate, UUID lastModifiedBy, LocalDateTime lastModifiedDate,
                      UUID id,
                      @NotBlank(message = "{I18nConstants.MESSAGE_ROLE_NAME_REQUIRED}")
                      @Size(min = 5, max = 20, message = "{I18nConstants.MESSAGE_ROLE_NAME_LENGTH}")
                      String name, Set<Permissions> permissions) implements Serializable {
    @Serial
    private static final long serialVersionUID = -1597317690891338318L;
}