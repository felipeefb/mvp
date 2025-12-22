package com.felipe.belo.mvp.usecase.user.port;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence abstraction for users.
 */
public interface UserRepository {
    /**
     * Finds a user by id.
     *
     * @param id user id
     * @return optional user
     */
    Optional<User> findById(UUID id);

    /**
     * Finds a user by e-mail (case-insensitive).
     *
     * @param email e-mail to search
     * @return optional user
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Finds a user by name (case-insensitive).
     *
     * @param name name to search
     * @return optional user
     */
    Optional<User> findByNameIgnoreCase(String name);

    /**
     * Searches users with filters/pagination.
     *
     * @param request search request
     * @param pageable paging options
     * @return paged users
     */
    Page<User> search(SearchRequestDto request, Pageable pageable);

    /**
     * Persists a user.
     *
     * @param user user to save
     * @return saved user
     */
    User save(User user);
}
