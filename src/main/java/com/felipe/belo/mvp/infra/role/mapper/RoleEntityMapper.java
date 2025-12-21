package com.felipe.belo.mvp.infra.role.mapper;

import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.infra.role.entity.RoleEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps between Role JPA entities and domain models.
 */
public class RoleEntityMapper {
    public Role toDomain(RoleEntity entity) {
        if (entity == null) {
            return null;
        }
        Role role = new Role();
        role.setId(entity.getId());
        role.setName(entity.getName());
        role.setPermissions(entity.getPermissions());
        role.setCreatedBy(entity.getCreatedBy());
        role.setCreatedDate(entity.getCreatedDate());
        role.setLastModifiedBy(entity.getLastModifiedBy());
        role.setLastModifiedDate(entity.getLastModifiedDate());
        role.setDeletedBy(entity.getDeletedBy());
        role.setDeletedAt(entity.getDeletedAt());
        return role;
    }

    public RoleEntity toEntity(Role role) {
        if (role == null) {
            return null;
        }
        RoleEntity entity = new RoleEntity();
        entity.setId(role.getId());
        entity.setName(role.getName());
        entity.setPermissions(role.getPermissions());
        entity.setDeletedBy(role.getDeletedBy());
        entity.setDeletedAt(role.getDeletedAt());
        return entity;
    }

    public List<Role> toDomainList(List<RoleEntity> entities) {
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}
