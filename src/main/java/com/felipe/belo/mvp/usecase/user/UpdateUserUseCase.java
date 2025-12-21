package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.command.UpdateUserCommand;

import java.util.UUID;

public interface UpdateUserUseCase {
    User update(UUID id, UpdateUserCommand command);
}
