package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;

import java.util.Optional;

/**
 * Use case for finding a user by username.
 */
public interface FindUserByUsernameUseCase {
    /**
     * Finds a user by username.
     *
     * @param username username to search
     * @return optional user
     */
    Optional<User> findByUsername(String username);
}
