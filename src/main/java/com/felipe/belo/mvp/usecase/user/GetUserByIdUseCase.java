package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;

import java.util.UUID;

/**
 * Use case for fetching a user by id with exception on absence.
 */
public interface GetUserByIdUseCase {
    /**
     * Finds a user or throws when not found.
     *
     * @param id user id
     * @return user
     */
    User findById(UUID id);
}
