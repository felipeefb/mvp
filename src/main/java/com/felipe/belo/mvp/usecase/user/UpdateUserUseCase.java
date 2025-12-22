package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.command.UpdateUserCommand;

import java.util.UUID;

/**
 * Use case for updating users.
 */
public interface UpdateUserUseCase {
    /**
     * Updates a user by id.
     *
     * @param id      user id
     * @param command update command
     * @return updated user
     */
    User update(UUID id, UpdateUserCommand command);
}
