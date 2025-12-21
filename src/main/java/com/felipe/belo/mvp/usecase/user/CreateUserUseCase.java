package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.command.CreateUserCommand;

public interface CreateUserUseCase {
    User create(CreateUserCommand command);
}
