package com.felipe.belo.mvp.core.config.module;

import com.felipe.belo.mvp.user.entity.UserEntity;
import com.felipe.belo.mvp.user.repository.UserRepository;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAwareImpl")
public class AuditAwareImpl implements AuditorAware<UUID> {

    private final UserRepository userRepository;

    public AuditAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticationValid(authentication)) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // 1. Optimization: If the principal is already our UserEntity, use it directly
        if (principal instanceof UserEntity userEntity) {
            return Optional.of(userEntity.getId());
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
                    .map(UserEntity::getId);
        }

        return Optional.empty();
    }

    private boolean isAuthenticationValid(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}