package com.felipe.belo.mvp.application.config.module;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.core.service.CurrentUserService;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Provides the current auditor (user id) for JPA auditing fields.
 */
@Component("auditorAwareImpl")
public class AuditAwareImpl implements AuditorAware<UUID> {

    private final CurrentUserService currentUserService;

    /**
     * Creates a new instance backed by the given repository.
     *
     * @param currentUserService service used to resolve the current authenticated user
     */
    public AuditAwareImpl(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    /**
     * Resolve the current authenticated user's id to populate auditing fields.
     *
     * @return the current user's id if available
     */
    @Override
    public Optional<UUID> getCurrentAuditor() {
        return currentUserService.getCurrentUser().map(User::getId);
    }
}
