package com.felipe.belo.mvp.infra.user.mapper;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import com.felipe.belo.mvp.infra.role.mapper.RoleEntityMapper;
import com.felipe.belo.mvp.infra.user.entity.UserEntity;

import java.util.Objects;

/**
 * Maps between User JPA entities and domain models.
 */
public class UserEntityMapper {
    private final RoleEntityMapper roleMapper = new RoleEntityMapper();

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setName(entity.getName());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setRole(roleMapper.toDomain(entity.getRole()));
        user.setCreatedBy(entity.getCreatedBy());
        user.setCreatedDate(entity.getCreatedDate());
        user.setLastModifiedBy(entity.getLastModifiedBy());
        user.setLastModifiedDate(entity.getLastModifiedDate());
        user.setDeletedBy(entity.getDeletedBy());
        user.setDeletedAt(entity.getDeletedAt());
        return user;
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        RoleEntity roleEntity = roleMapper.toEntity(user.getRole());
        entity.setRole(roleEntity);
        entity.setDeletedBy(user.getDeletedBy());
        entity.setDeletedAt(user.getDeletedAt());
        return entity;
    }

    public void updateEntity(UserEntity entity, User user) {
        if (entity == null || user == null) {
            return;
        }
        if (user.getName() != null) {
            entity.setName(user.getName());
        }
        if (user.getEmail() != null) {
            entity.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            entity.setPassword(user.getPassword());
        }
        if (user.getRole() != null) {
            entity.setRole(roleMapper.toEntity(user.getRole()));
        }
        if (Objects.nonNull(user.getDeletedBy())) {
            entity.setDeletedBy(user.getDeletedBy());
        }
        if (Objects.nonNull(user.getDeletedAt())) {
            entity.setDeletedAt(user.getDeletedAt());
        }
    }
}
