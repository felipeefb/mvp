package com.felipe.belo.mvp.role.dto;

import com.felipe.belo.mvp.utils.permissions.Permissions;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link com.felipe.belo.mvp.role.entity.Role}
 */
public record RoleListDto(UUID id, String name, Set<Permissions> permissions) implements Serializable {
    @Serial
    private static final long serialVersionUID = -8834198609984273766L;
}