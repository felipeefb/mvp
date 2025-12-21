package com.felipe.belo.mvp.usecase.core.service;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
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

    /**
     * Creates a new service instance.
     *
     * @param userRepository repository used to resolve users
     */
    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the current authenticated {@link User}, if available.
     *
     * @return an optional containing the current user or empty if unauthenticated
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!isAuthenticationValid(authentication)) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // 1. If the principal is already our UserEntity
        if (principal instanceof User user) {
            return Optional.of(user);
        }

        // 2. Extract username
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else if (principal instanceof String principalName) {
            username = principalName;
        } else {
            username = authentication.getName();
        }

        // 3. Find by email
        if (username != null) {
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
    /**
     * Gets the current authenticated user or throws an exception.
     *
     * @return the authenticated {@link UserEntity}
     * @throws IllegalStateException if no user is authenticated
     */
    public User getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }

    /**
     * Checks whether the given authentication is present and valid.
     *
     * @param authentication the authentication to check
     * @return true if authenticated
     */
    private boolean isAuthenticationValid(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}
