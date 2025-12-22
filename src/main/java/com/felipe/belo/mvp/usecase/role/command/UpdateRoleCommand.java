package com.felipe.belo.mvp.usecase.role.command;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;

import java.util.Set;

/**
 * Command payload to update a role.
 *
 * @param name        new role name (nullable)
 * @param permissions new permissions (nullable)
 */
public record UpdateRoleCommand(String name, Set<Permissions> permissions) {
}
