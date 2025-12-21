package com.felipe.belo.mvp.usecase.user.command;

import java.util.UUID;

public record CreateUserCommand(String name, String email, String password, UUID roleId) {
}
