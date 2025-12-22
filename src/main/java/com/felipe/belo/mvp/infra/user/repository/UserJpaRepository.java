package com.felipe.belo.mvp.infra.user.repository;

import com.felipe.belo.mvp.infra.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link UserEntity} instances.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {
    /**
     * Finds a user by e-mail ignoring case.
     *
     * @param email e-mail to search
     * @return optional user
     */
    Optional<UserEntity> findByEmailIgnoreCase(String email);

    /**
     * Finds a user by name ignoring case.
     *
     * @param name name to search
     * @return optional user
     */
    Optional<UserEntity> findByNameIgnoreCase(String name);

}
