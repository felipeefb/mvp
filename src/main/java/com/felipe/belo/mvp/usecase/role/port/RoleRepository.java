package com.felipe.belo.mvp.usecase.role.port;

import com.felipe.belo.mvp.core.domain.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {
    Optional<Role> findById(UUID id);

    Optional<Role> findByNormalizedName(String name);

    List<Role> findAllByDeletedAtIsNull();

    Page<Role> search(String search, boolean includeDeleted, Pageable pageable);

    Role save(Role role);

    List<com.felipe.belo.mvp.core.utils.permissions.Permissions> findPermissions(UUID roleId);
}
