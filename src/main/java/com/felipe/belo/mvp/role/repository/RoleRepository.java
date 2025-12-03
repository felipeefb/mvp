package com.felipe.belo.mvp.role.repository;

import com.felipe.belo.mvp.role.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    @Query(value = "SELECT * FROM role " +
    "WHERE REPLACE(LOWER(unaccent(name)), ' ', '') = REPLACE(LOWER(unaccent(:name)), ' ', '')", nativeQuery = true)
    Optional<Role> findByNormalizedName(@Param("name") String name);
    List<Role> findAllByDeletedAtIsNull();
}