package com.felipe.belo.mvp.core.config.module;

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

        if (isUserDetailsAuthentication(authentication)) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByEmailIgnoreCase(username)
                    .map(user -> user.getId());
        }
        return Optional.empty();
    }

    private boolean isAuthenticationValid(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private boolean isUserDetailsAuthentication(Authentication authentication) {
        return authentication.getPrincipal() instanceof UserDetails;
    }
}