package com.felipe.belo.mvp.application.shared.api;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.LookupUserByIdUseCase;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resolves user metadata such as e-mail addresses given a user id.
 */
@Component
public class UserEmailResolver {

    private final LookupUserByIdUseCase userLookupUseCase;

    /**
     * Creates a resolver using the provided lookup use case.
     *
     * @param userLookupUseCase use case to find users by id
     */
    public UserEmailResolver(LookupUserByIdUseCase userLookupUseCase) {
        this.userLookupUseCase = userLookupUseCase;
    }

    /**
     * Resolves a user e-mail by id, returning null when absent.
     *
     * @param userId target user id
     * @return user e-mail or null
     */
    public String resolveUserEmail(UUID userId) {
        if (userId == null) {
            return null;
        }
        return userLookupUseCase.findOptionalById(userId)
                .map(User::getEmail)
                .orElse(null);
    }
}
