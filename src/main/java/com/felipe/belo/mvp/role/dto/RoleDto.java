package com.felipe.belo.mvp.role.dto;

import com.felipe.belo.mvp.utils.permissions.Permissions;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link com.felipe.belo.mvp.role.entity.Role}
 */
public record RoleDto(UUID createdBy, LocalDateTime createdDate, UUID lastModifiedBy, LocalDateTime lastModifiedDate,
                      UUID id,
                      @NotBlank(message = "{I18nConstants.MESSAGE_ROLE_NAME_REQUIRED}")
                      @Size(min = 5, max = 20, message = "{I18nConstants.MESSAGE_ROLE_NAME_LENGTH}")
                      String name, Set<Permissions> permissions) implements Serializable {
    @Serial
    private static final long serialVersionUID = -1597317690891338318L;
}