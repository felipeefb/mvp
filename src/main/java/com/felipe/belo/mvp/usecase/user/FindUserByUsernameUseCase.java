package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;

import java.util.Optional;

public interface FindUserByUsernameUseCase {
    Optional<User> findByUsername(String username);
}
