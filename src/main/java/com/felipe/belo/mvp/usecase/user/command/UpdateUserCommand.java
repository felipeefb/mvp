package com.felipe.belo.mvp.usecase.user.command;

import java.util.UUID;

public record UpdateUserCommand(String name, String email, String password, UUID roleId) {
}
