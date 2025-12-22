package com.felipe.belo.mvp.infra.role.repository;

import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID>, JpaSpecificationExecutor<RoleEntity> {

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
}
