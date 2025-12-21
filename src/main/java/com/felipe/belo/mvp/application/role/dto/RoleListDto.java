package com.felipe.belo.mvp.application.role.dto;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Lightweight DTO for listing {@link com.felipe.belo.mvp.core.domain.model.Role} entries.
 *
 * @param id          the role id
 * @param name        the role name
 * @param permissions the permissions assigned to the role
 * @param DeletedAt   the role deletion timestamp (optional)
 */
public record RoleListDto(UUID id, String name, Set<Permissions> permissions, LocalDateTime DeletedAt) implements Serializable {
    @Serial
    private static final long serialVersionUID = -8834198609984273766L;
}