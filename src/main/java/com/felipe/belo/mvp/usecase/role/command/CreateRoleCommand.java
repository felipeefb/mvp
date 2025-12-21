package com.felipe.belo.mvp.usecase.role.command;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;

import java.util.Set;

public record CreateRoleCommand(String name, Set<Permissions> permissions) {
}
