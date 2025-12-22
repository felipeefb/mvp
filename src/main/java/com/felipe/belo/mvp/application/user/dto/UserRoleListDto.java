package com.felipe.belo.mvp.application.user.dto;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Lightweight role summary used inside user list payloads.
 *
 * @param id         role identifier
 * @param name       role name
 * @param permissions granted permissions
 * @param deletedAt  soft-delete timestamp, if any
 */
public record UserRoleListDto(UUID id, String name, Set<Permissions> permissions,
                              LocalDateTime deletedAt) implements Serializable {
    @Serial
    private static final long serialVersionUID = -7086339135968380651L;
}
