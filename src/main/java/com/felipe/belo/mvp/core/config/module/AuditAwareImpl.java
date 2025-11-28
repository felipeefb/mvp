package com.felipe.belo.mvp.core.config.module;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticationValid(authentication)) {
            return Optional.empty();
        }

        if (isUserDetailsAuthentication(authentication)) {
            return Optional.of(((UserDetails) authentication.getPrincipal()).getUsername());
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