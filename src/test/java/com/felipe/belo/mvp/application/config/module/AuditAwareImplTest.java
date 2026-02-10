package com.felipe.belo.mvp.application.config.module;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.core.service.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditAwareImplTest {

    @Test
    void returnsCurrentUserId() {
        UUID id = UUID.randomUUID();
        CurrentUserService svc = Mockito.mock(CurrentUserService.class);
        Mockito.when(svc.getCurrentUser()).thenReturn(Optional.of(new User(id, "n", "e", "p", null)));
        AuditAwareImpl audit = new AuditAwareImpl(svc);

        assertThat(audit.getCurrentAuditor()).contains(id);
    }

    @Test
    void returnsEmptyWhenNoUser() {
        CurrentUserService svc = Mockito.mock(CurrentUserService.class);
        Mockito.when(svc.getCurrentUser()).thenReturn(Optional.empty());
        AuditAwareImpl audit = new AuditAwareImpl(svc);

        assertThat(audit.getCurrentAuditor()).isEmpty();
    }
}
