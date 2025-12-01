package com.felipe.belo.mvp.role.service;

import com.felipe.belo.mvp.core.exception.BusinessException;
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

import java.util.List;
import java.util.UUID;

/**
 * Service class responsible for handling all business logic related to the Role entity.
 * It provides methods for creating, updating, deleting, and retrieving roles, along with
 * handling validation and constraints on the entity during operations.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleDto create(CreateRoleDto createRoleDto) {
        Role existing = this.roleRepository.findByName(createRoleDto.name()).orElse(null);
        this.checkConstraints(existing, null);
        Role role = this.roleMapper.toEntity(createRoleDto);
        return this.roleMapper.toDto(this.roleRepository.save(role));
    }

    public RoleDto update(UUID id, UpdateRoleDto updateRoleDto) {
        Role existing = this.roleRepository.findById(id).orElse(null);
        CheckIsNull(existing);
        this.checkConstraints(existing, id);
        Role role = this.roleMapper.partialUpdate(updateRoleDto, existing);
        return this.roleMapper.toDto(this.roleRepository.save(role));
    }

    public void delete(UUID id) {
        Role existing = this.roleRepository.findById(id).orElse(null);
        CheckIsNull(existing);
        this.roleRepository.delete(existing);
    }

    public List<RoleListDto> findAll() {
        return this.roleMapper.toDtoList(this.roleRepository.findAll());
    }

    private static void CheckIsNull(Role existing) {
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