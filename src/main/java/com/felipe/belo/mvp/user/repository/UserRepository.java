package com.felipe.belo.mvp.user.repository;

import com.felipe.belo.mvp.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link UserEntity} instances.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    /**
     * Finds a user by e-mail ignoring case.
     *
     * @param email e-mail to search
     * @return optional user
     */
    Optional<UserEntity> findByEmailIgnoreCase(String email);
}