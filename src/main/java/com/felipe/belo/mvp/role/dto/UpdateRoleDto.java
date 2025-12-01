package com.felipe.belo.mvp.role.dto;

import com.felipe.belo.mvp.utils.permissions.Permissions;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link com.felipe.belo.mvp.role.entity.Role}
 */
public record UpdateRoleDto(String name, Set<Permissions> permissions) implements Serializable {
    @Serial
    private static final long serialVersionUID = 2703200338095285002L;
}