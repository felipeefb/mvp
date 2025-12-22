package com.felipe.belo.mvp.usecase.role.command;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;

import java.util.Set;

/**
 * Command payload to create a role.
 *
 * @param name        role name
 * @param permissions granted permissions
 */
public record CreateRoleCommand(String name, Set<Permissions> permissions) {
}
