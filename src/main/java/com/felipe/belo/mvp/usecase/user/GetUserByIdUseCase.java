package com.felipe.belo.mvp.usecase.user;

import com.felipe.belo.mvp.core.domain.model.User;

import java.util.UUID;

public interface GetUserByIdUseCase {
    User findById(UUID id);
}
