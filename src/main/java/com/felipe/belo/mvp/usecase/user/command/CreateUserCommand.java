package com.felipe.belo.mvp.usecase.user.command;

import java.util.UUID;

/**
 * Command payload to create a user.
 *
 * @param name     display name
 * @param email    e-mail address
 * @param password raw password
 * @param roleId   role identifier
 */
public record CreateUserCommand(String name, String email, String password, UUID roleId) {
}
