package com.felipe.belo.mvp.usecase.user.command;

import java.util.UUID;

/**
 * Command payload to update a user.
 *
 * @param name     new name (nullable)
 * @param email    new e-mail (nullable)
 * @param password new password (nullable)
 * @param roleId   new role id (nullable)
 */
public record UpdateUserCommand(String name, String email, String password, UUID roleId) {
}
