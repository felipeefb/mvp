package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.command.CreateUserCommand;

/**
 * Use case for creating users.
 */
public interface CreateUserUseCase {
    /**
     * Creates a user.
     *
     * @param command create command
     * @return created user
     */
    User create(CreateUserCommand command);
}
