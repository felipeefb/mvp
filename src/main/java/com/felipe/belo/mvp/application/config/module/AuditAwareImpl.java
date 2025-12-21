package com.felipe.belo.mvp.application.config.module;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Provides the current auditor (user id) for JPA auditing fields.
 */
@Component("auditorAwareImpl")
public class AuditAwareImpl implements AuditorAware<UUID> {

    private final UserRepository userRepository;

    /**
     * Creates a new instance backed by the given repository.
     *
     * @param userRepository repository used to resolve users by email
     */
    public AuditAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Resolve the current authenticated user's id to populate auditing fields.
     *
     * @return the current user's id if available
     */
    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticationValid(authentication)) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // 1. Optimization: If the principal is already our domain User, use it directly
        if (principal instanceof User user) {
            return Optional.of(user.getId());
        }

        // 2. Extract username from various principal types
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String principalName) {
            username = principalName;
        } else {
            // Fallback
            username = authentication.getName();
        }

        // 3. Try to find the user by email (assuming username is email)
        if (username != null) {
            return userRepository.findByEmailIgnoreCase(username)
                    .map(User::getId);
        }

        return Optional.empty();
    }

    /**
     * Checks whether the given authentication is non-null and authenticated.
     *
     * @param authentication the authentication instance
     * @return true if valid
     */
    private boolean isAuthenticationValid(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}
