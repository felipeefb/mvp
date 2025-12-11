package com.felipe.belo.mvp.user.repository;

import com.felipe.belo.mvp.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Searches users by optional search term, with pagination, and optional inclusion of soft-deleted entries.
     * Uses PostgreSQL unaccent for accent-insensitive search across name and email fields.
     *
     * @param search search term to filter by name or email
     * @param includeDeleted whether to include soft-deleted users
     * @param pageable pagination information
     * @return page of users
     */
    @Query(value = "SELECT * FROM users u " +
            "WHERE (:includeDeleted = true OR u.deleted_at IS NULL) " +
            "AND (:search IS NULL OR " +
            "     unaccent(lower(u.name)) LIKE CONCAT('%', unaccent(lower(:search)), '%') OR " +
            "     unaccent(lower(u.email)) LIKE CONCAT('%', unaccent(lower(:search)), '%'))",
            countQuery = "SELECT count(*) FROM users u " +
                    "WHERE (:includeDeleted = true OR u.deleted_at IS NULL) " +
                    "AND (:search IS NULL OR " +
                    "     unaccent(lower(u.name)) LIKE CONCAT('%', unaccent(lower(:search)), '%') OR " +
                    "     unaccent(lower(u.email)) LIKE CONCAT('%', unaccent(lower(:search)), '%'))",
            nativeQuery = true)
    Page<UserEntity> search(@Param("search") String search,
                            @Param("includeDeleted") boolean includeDeleted,
                            Pageable pageable);
}