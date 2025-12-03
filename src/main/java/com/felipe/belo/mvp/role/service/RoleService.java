package com.felipe.belo.mvp.role.service;

import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.service.CurrentUserService;
import com.felipe.belo.mvp.user.entity.UserEntity;
import com.felipe.belo.mvp.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.role.dto.RoleDto;
import com.felipe.belo.mvp.role.dto.RoleListDto;
import com.felipe.belo.mvp.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.role.entity.Role;
import com.felipe.belo.mvp.role.mapper.RoleMapper;
import com.felipe.belo.mvp.role.repository.RoleRepository;
import com.felipe.belo.mvp.utils.I18nConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing roles in the system.
 *
 * This class provides methods to create, update, delete, and retrieve roles. It also includes
 * logic to validate constraints and handle business rules related to roles.
 *
 * Key functionalities:
 * - Create a new role.
 * - Update an existing role.
 * - Delete a role by its ID.
 * - Retrieve a list of all roles that are not marked as deleted.
 *
 * The class uses RoleRepository for database operations and RoleMapper for mapping between
 * entities and DTOs.
 *
 * Transactions:
 * - Methods that modify data are marked as @Transactional to ensure consistency and rollback in case of errors.
 *
 * Exceptions:
 * - Throws BusinessException for specific business rule violations, such as role name conflicts or missing roles.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    private final CurrentUserService currentUserService;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper, CurrentUserService currentUserService) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public RoleDto create(CreateRoleDto createRoleDto) {
        Role existing = this.roleRepository.findByNormalizedName(createRoleDto.name()).orElse(null);
        this.checkConstraints(existing, null);
        Role role = this.roleMapper.toEntity(createRoleDto);
        return this.roleMapper.toDto(this.roleRepository.save(role));
    }

    @Transactional
    public RoleDto update(UUID id, UpdateRoleDto updateRoleDto) {
        Role existing = this.roleRepository.findById(id).orElse(null);
        checkIsNull(existing);
        this.checkConstraints(existing, id);
        Role role = this.roleMapper.partialUpdate(updateRoleDto, existing);
        return this.roleMapper.toDto(this.roleRepository.save(role));
    }


    public RoleDto findById(UUID id) {
        Role role = this.roleRepository.findById(id).orElse(null);
        checkIsNull(role);
        return this.roleMapper.toDto(role);
    }

    @Transactional
    public void delete(UUID id) {
        Role existing = this.roleRepository.findById(id).orElse(null);
        checkIsNull(existing);
        existing.setDeletedAt(java.time.LocalDateTime.now());
        existing.setDeletedBy(currentUserService.getCurrentUser().map(UserEntity::getId).orElse(null));
        this.roleRepository.save(existing);
    }

    public List<RoleListDto> findAll() {
        return this.roleMapper.toDtoList(this.roleRepository.findAllByDeletedAtIsNull());
    }

    private static void checkIsNull(Role existing) {
        if (existing == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND);
        }
    }

    private void checkConstraints(Role role, UUID id) {
        if (role != null && (id == null || !role.getId().equals(id))) {
            throw new BusinessException(HttpStatus.CONFLICT, I18nConstants.MESSAGE_ROLE_NAME_EXISTS);
        }
    }
}