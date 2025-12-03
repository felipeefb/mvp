package com.felipe.belo.mvp.core.service;

import com.felipe.belo.mvp.user.entity.UserEntity;
import com.felipe.belo.mvp.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to retrieve the current authenticated user.
 * This service provides a centralized way to access the currently logged-in user's information.
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Gets the current authenticated user.
     *
     * @return Optional containing the UserEntity if authenticated, empty otherwise
     */
    public Optional<UserEntity> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticationValid(authentication)) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return userRepository.findByEmailIgnoreCase(username);
        }

        return Optional.empty();
    }

    /**
     * Gets the current authenticated user or throws an exception.
     *
     * @return the UserEntity
     * @throws IllegalStateException if no user is authenticated
     */
    public UserEntity getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    private boolean isAuthenticationValid(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}
