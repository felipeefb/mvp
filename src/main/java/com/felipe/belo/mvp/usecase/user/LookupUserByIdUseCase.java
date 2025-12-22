package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Lookup use case for resolving users by id without throwing.
 */
public interface LookupUserByIdUseCase {
    /**
     * Finds a user by id optionally.
     *
     * @param id user id
     * @return optional user
     */
    Optional<User> findOptionalById(UUID id);
}
