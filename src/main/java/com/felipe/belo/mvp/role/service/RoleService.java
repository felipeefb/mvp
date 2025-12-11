package com.felipe.belo.mvp.role.service;

import com.felipe.belo.mvp.core.domain.page.dto.SearchFieldDTO;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Service class for managing roles in the system.
 * This class provides methods to create, update, delete, and retrieve roles. It also includes
 * logic to validate constraints and handle business rules related to roles.
 * Key functionalities:
 * - Create a new role.
 * - Update an existing role.
 * - Delete a role by its ID.
 * - Retrieve a list of all roles that are not marked as deleted.
 * The class uses RoleRepository for database operations and RoleMapper for mapping between
 * entities and DTOs.
 * Transactions:
 * - Methods that modify data are marked as @Transactional to ensure consistency and rollback in case of errors.
 * Exceptions:
 * - Throws BusinessException for specific business rule violations, such as role name conflicts or missing roles.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    private final CurrentUserService currentUserService;

    /**
     * Creates a new role service.
     * @param roleRepository repository for roles
     * @param roleMapper mapper for entity/DTO conversions
     * @param currentUserService service to resolve the current user
     */
    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper, CurrentUserService currentUserService) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.currentUserService = currentUserService;
    }

    /**
     * Creates a new role after validating constraints.
     * @param createRoleDto input payload
     * @return created role
     */
    @Transactional
    public RoleDto create(CreateRoleDto createRoleDto) {
        Role existing = this.roleRepository.findByNormalizedName(createRoleDto.name()).orElse(null);
        this.checkConstraints(existing, null);
        Role role = this.roleMapper.toEntity(createRoleDto);
        return this.roleMapper.toDto(this.roleRepository.save(role));
    }

    /**
     * Updates an existing role.
     * @param id role id
     * @param updateRoleDto input payload
     * @return updated role
     */
    @Transactional
    public RoleDto update(UUID id, UpdateRoleDto updateRoleDto) {
        Role existing = this.roleRepository.findById(id).orElse(null);
        checkIsNull(existing);
        this.checkConstraints(existing, id);
        Role role = this.roleMapper.partialUpdate(updateRoleDto, existing);
        return this.roleMapper.toDto(this.roleRepository.save(role));
    }


    /**
     * Retrieves a role by its identifier.
     * @param id role id
     * @return role details
     */
    public RoleDto findById(UUID id) {
        Role role = this.roleRepository.findById(id).orElse(null);
        checkIsNull(role);
        return this.roleMapper.toDto(role);
    }

    /**
     * Soft-deletes a role by setting deletedAt and deletedBy.
     * @param id role id
     */
    @Transactional
    public void delete(UUID id) {
        Role existing = this.roleRepository.findById(id).orElse(null);
        checkIsNull(existing);
        existing.setDeletedAt(java.time.LocalDateTime.now());
        existing.setDeletedBy(currentUserService.getCurrentUser().map(UserEntity::getId).orElse(null));
        this.roleRepository.save(existing);
    }

    /**
     * Returns all non-deleted roles.
     * @return list of roles
     */
    public List<RoleListDto> findAll() {
        return this.roleMapper.toDtoList(this.roleRepository.findAllByDeletedAtIsNull());
    }

    /**
     * Searches and lists roles with pagination and filtering based on the provided search request.
     *
     * @param searchRequest search request containing filters, pagination, and soft-delete inclusion
     * @return paged result of RoleListDto
     */
    public Page<RoleListDto> findAll(SearchRequestDTO searchRequest) {
        Pageable pageable = PageRequest.of(
                Math.max(searchRequest.page(), 0),
                Math.max(searchRequest.size(), 1)
        );

        // Extract search term from filters if present
        String searchTerm = extractSearchTerm(searchRequest);

        Page<Role> roles = this.roleRepository.search(
                searchTerm,
                searchRequest.includeDeleted(),
                pageable
        );
        List<RoleListDto> content = this.roleMapper.toDtoList(roles.getContent());
        return new PageImpl<>(content, pageable, roles.getTotalElements());
    }

    /**
     * Extracts the search term from the SearchRequestDTO filters.
     * Looks for filters on 'name' field with 'contains' operation.
     *
     * @param searchRequest the search request
     * @return the search term or null if not found
     */
    private String extractSearchTerm(SearchRequestDTO searchRequest) {
        if (searchRequest.filters() == null || searchRequest.filters().isEmpty()) {
            return null;
        }

        return searchRequest.filters().stream()
                .filter(filter -> "name".equals(filter.field())
                        && ("contains".equalsIgnoreCase(filter.operation()) || ":".equals(filter.operation())))
                .map(SearchFieldDTO::value)
                .findFirst()
                .orElse(null);
    }

    /**
     * Ensures the given role exists.
     * @param existing role or null
     */
    private static void checkIsNull(Role existing) {
        if (existing == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND);
        }
    }

    /**
     * Validates role constraints (e.g., unique name).
     *
     * @param role existing role with same name, if any
     * @param id   current id for updates, null for create
     */
    private void checkConstraints(Role role, UUID id) {
        if (role != null && (id == null || !role.getId().equals(id))) {
            throw new BusinessException(HttpStatus.CONFLICT, I18nConstants.MESSAGE_ROLE_NAME_EXISTS);
        }
    }
}