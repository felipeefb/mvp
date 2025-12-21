package com.felipe.belo.mvp.usecase.user.port;

import com.felipe.belo.mvp.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByNameIgnoreCase(String name);

    Page<User> search(String search, boolean includeDeleted, Pageable pageable);

    User save(User user);
}
