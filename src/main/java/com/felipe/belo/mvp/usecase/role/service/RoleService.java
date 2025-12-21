package com.felipe.belo.mvp.usecase.role.service;

import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.dto.SearchFieldDTO;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import com.felipe.belo.mvp.usecase.core.service.CurrentUserService;
import com.felipe.belo.mvp.usecase.role.RoleUseCase;
import com.felipe.belo.mvp.usecase.role.command.CreateRoleCommand;
import com.felipe.belo.mvp.usecase.role.command.UpdateRoleCommand;
import com.felipe.belo.mvp.usecase.role.port.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Service class for managing roles in the system.
 * This class provides methods to create, update, delete, and retrieve roles. It also includes
 * logic to validate constraints and handle business rules related to roles.
 * Key functionalities:
 * - Create a new role.
 * - Update an existing role.
 * - Delete a role by its ID.
 * - Retrieve a list of all roles that are not marked as deleted.
 * The class uses RoleRepository for persistence and operates on pure domain models.
 * Transactions:
 * - Methods that modify data are marked as @Transactional to ensure consistency and rollback in case of errors.
 * Exceptions:
 * - Throws BusinessException for specific business rule violations, such as role name conflicts or missing roles.
 */
@Service
public class RoleService implements RoleUseCase {

    private final RoleRepository roleRepository;

    private final CurrentUserService currentUserService;

    /**
     * Creates a new role service.
     * @param roleRepository repository for roles
     * @param roleMapper mapper for entity/DTO conversions
     * @param currentUserService service to resolve the current user
     */
    public RoleService(RoleRepository roleRepository, CurrentUserService currentUserService) {
        this.roleRepository = roleRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Creates a new role after validating constraints.
     * @param createRoleDto input payload
     * @return created role
     */
    @Transactional
    public Role create(CreateRoleCommand command) {
        Role existing = this.roleRepository.findByNormalizedName(command.name()).orElse(null);
        this.checkConstraints(existing, null);
        Role role = new Role();
        role.setName(command.name());
        role.setPermissions(command.permissions());
        return this.roleRepository.save(role);
    }

    /**
     * Updates an existing role.
     * @param id role id
     * @param updateRoleDto input payload
     * @return updated role
     */
    @Transactional
    public Role update(UUID id, UpdateRoleCommand command) {
        Role existing = this.roleRepository.findById(id).orElse(null);
        checkIsNull(existing);
        this.checkConstraints(existing, id);
        if (command.name() != null) {
            existing.setName(command.name());
        }
        if (command.permissions() != null) {
            existing.setPermissions(command.permissions());
        }
        return this.roleRepository.save(existing);
    }


    /**
     * Retrieves a role by its identifier.
     * @param id role id
     * @return role details
     */
    public Role findById(UUID id) {
        Role role = this.roleRepository.findById(id).orElse(null);
        checkIsNull(role);
        return role;
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
        existing.setDeletedBy(currentUserService.getCurrentUser().map(User::getId).orElse(null));
        this.roleRepository.save(existing);
    }

    /**
     * Returns all non-deleted roles.
     * @return list of roles
     */
    public List<Role> findAll() {
        return this.roleRepository.findAllByDeletedAtIsNull();
    }

    /**
     * Searches and lists roles with pagination and filtering based on the provided search request.
     *
     * @param searchRequest search request containing filters, pagination, and soft-delete inclusion
     * @return paged result of RoleListDto
     */
    public Page<Role> findAll(SearchRequestDTO searchRequest) {
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
        return new PageImpl<>(roles.getContent(), pageable, roles.getTotalElements());
    }

    @Override
    public Map<String, Set<String>> findPermissionGroups(UUID id) {
        Role existing = this.roleRepository.findById(id).orElse(null);
        checkIsNull(existing);
        List<Permissions> permissions = roleRepository.findPermissions(id);
        Map<String, Set<String>> groups = new LinkedHashMap<>();
        for (Permissions permission : permissions) {
            String[] parts = permission.name().split("_", 2);
            String group = parts[0];
            String action = parts.length > 1 ? parts[1] : permission.name();
            groups.computeIfAbsent(group, k -> new java.util.LinkedHashSet<>()).add(action);
        }
        return groups;
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
