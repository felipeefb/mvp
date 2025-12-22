package com.felipe.belo.mvp.application.user.dto;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Role details exposed within user-facing DTOs to avoid cross-module coupling.
 *
 * @param createdBy        creator user id
 * @param createdDate      creation timestamp
 * @param lastModifiedBy   last modifier e-mail
 * @param lastModifiedDate last modification timestamp
 * @param id               role id
 * @param name             role name
 * @param permissions      granted permissions
 */
public record UserRoleDto(UUID createdBy, LocalDateTime createdDate, String lastModifiedBy, LocalDateTime lastModifiedDate,
                          UUID id, String name, Set<Permissions> permissions) implements Serializable {
    @Serial
    private static final long serialVersionUID = -3907112908198843871L;
}
