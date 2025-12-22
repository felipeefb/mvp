package com.felipe.belo.mvp.infra.role.repository;

import com.felipe.belo.mvp.core.domain.SearchSpecification;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import com.felipe.belo.mvp.infra.role.mapper.RoleEntityMapper;
import com.felipe.belo.mvp.usecase.role.port.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA-backed implementation of the role repository port.
 */
@Repository
public class RoleRepositoryAdapter implements RoleRepository {
    private final RoleJpaRepository roleJpaRepository;
    private final RoleEntityMapper roleMapper;

    /**
     * Creates a new adapter.
     *
     * @param roleJpaRepository JPA repository
     * @param roleMapper        mapper for entity/domain conversion
     */
    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository, RoleEntityMapper roleMapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleJpaRepository.findById(id).map(roleMapper::toDomain);
    }

    @Override
    public Optional<Role> findByNormalizedName(String name) {
        return roleJpaRepository.findByNormalizedName(name).map(roleMapper::toDomain);
    }

    @Override
    public List<Role> findAllByDeletedAtIsNull() {
        return roleMapper.toDomainList(roleJpaRepository.findAllByDeletedAtIsNull());
    }

    @Override
    public Page<Role> search(SearchRequestDto request, Pageable pageable) {
        Page<RoleEntity> page = roleJpaRepository.findAll(SearchSpecification.build(request, roleMapper), pageable);
        List<Role> content = roleMapper.toDomainList(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public Role save(Role role) {
        RoleEntity entity;
        if (role.getId() != null) {
            entity = roleJpaRepository.findById(role.getId()).orElseGet(RoleEntity::new);
            entity.setId(role.getId());
            entity.setName(role.getName());
            entity.setPermissions(role.getPermissions());
            entity.setDeletedBy(role.getDeletedBy());
            entity.setDeletedAt(role.getDeletedAt());
        } else {
            entity = roleMapper.toEntity(role);
        }
        RoleEntity saved = roleJpaRepository.save(entity);
        return roleMapper.toDomain(saved);
    }

}
