package com.felipe.belo.mvp.infra.role.repository;

import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link RoleEntity} entities.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {

    /**
     * Finds a role by a normalized form of its name (lowercased, unaccented, and without spaces).
     *
     * @param name the input name to normalize and search
     * @return an optional containing the role if found
     */
    @Query(value = "SELECT * " +
                    "FROM role " +
                    "WHERE REPLACE(LOWER(unaccent(name)), ' ', '') = " +
            "REPLACE(LOWER(unaccent(:name)), ' ', '')", nativeQuery = true)
    Optional<RoleEntity> findByNormalizedName(@Param("name") String name);

    /**
     * Retrieves all roles that are not soft-deleted.
     *
     * @return list of active roles
     */
    List<RoleEntity> findAllByDeletedAtIsNull();

    /**
     * Searches roles by (optional) name term, with pagination, and optional inclusion of soft-deleted entries.
     * Uses PostgreSQL unaccent for accent-insensitive search.
     */
    @Query(value = "SELECT * FROM role r " +
            "WHERE (:includeDeleted = true OR r.deleted_at IS NULL) " +
            "AND (:search IS NULL OR unaccent(lower(r.name)) LIKE CONCAT('%', unaccent(lower(:search)), '%'))",
            countQuery = "SELECT count(*) FROM role r " +
                    "WHERE (:includeDeleted = true OR r.deleted_at IS NULL) " +
                    "AND (:search IS NULL OR unaccent(lower(r.name)) LIKE CONCAT('%', unaccent(lower(:search)), '%'))",
            nativeQuery = true)
    Page<RoleEntity> search(@Param("search") String search,
                       @Param("includeDeleted") boolean includeDeleted,
                       Pageable pageable);

    /**
     * Retrieves permissions for a role from the join table.
     *
     * @param roleId role id
     * @return permission names
     */
    @Query(value = "SELECT permission FROM role_permission WHERE role_id = :roleId", nativeQuery = true)
    List<String> findPermissionNamesByRoleId(@Param("roleId") UUID roleId);
}
