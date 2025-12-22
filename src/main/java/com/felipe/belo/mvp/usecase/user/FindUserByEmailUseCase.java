package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;

import java.util.Optional;

/**
 * Use case for finding a user by e-mail.
 */
public interface FindUserByEmailUseCase {
    /**
        * Finds a user by e-mail.
        *
        * @param email e-mail to search
        * @return optional user
        */
    Optional<User> findByEmail(String email);
}
