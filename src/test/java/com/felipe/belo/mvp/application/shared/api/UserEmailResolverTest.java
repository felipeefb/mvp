package com.felipe.belo.mvp.application.shared.api;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.LookupUserByIdUseCase;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserEmailResolverTest {

    @Test
    void resolvesEmailWhenUserExists() {
        LookupUserByIdUseCase lookup = id -> Optional.of(new User(id, "n", "user@test.com", "p", null));
        UserEmailResolver resolver = new UserEmailResolver(lookup);
        assertThat(resolver.resolveUserEmail(UUID.randomUUID())).isEqualTo("user@test.com");
    }

    @Test
    void returnsNullWhenIdMissing() {
        LookupUserByIdUseCase lookup = id -> Optional.empty();
        UserEmailResolver resolver = new UserEmailResolver(lookup);
        assertThat(resolver.resolveUserEmail(null)).isNull();
    }
}
