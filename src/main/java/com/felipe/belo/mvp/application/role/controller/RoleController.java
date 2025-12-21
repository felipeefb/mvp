package com.felipe.belo.mvp.application.role.controller;

import com.felipe.belo.mvp.application.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleListDto;
import com.felipe.belo.mvp.application.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.application.role.mapper.RoleDtoMapper;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import com.felipe.belo.mvp.usecase.role.RoleUseCase;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing roles.
 */
@RestController
@RequestMapping("/api/v1/roles")
@Validated
public class RoleController {

    private final RoleUseCase roleUseCase;
    private final RoleDtoMapper mapper = new RoleDtoMapper();

    /**
     * Creates a new controller instance.
     *
     * @param roleUseCase the role use case
     */
    public RoleController(RoleUseCase roleUseCase) {
        this.roleUseCase = roleUseCase;
    }

    /**
     * Creates a new role.
     *
     * @param roleDto request body
     * @return the created role
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RoleDto> createRole(@RequestBody @Validated CreateRoleDto roleDto) {
        Role created = this.roleUseCase.create(mapper.toCreateCommand(roleDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    /**
     * Lists roles with search, pagination, and filtering based on the provided search request.
     * @param searchRequest search request containing filters, pagination, and soft-delete inclusion
     * @return a paginated list of roles
     */
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_LIST')")
    public ResponseEntity<Page<RoleListDto>> listRoles(@RequestBody @Validated SearchRequestDTO searchRequest) {
        Page<Role> result = this.roleUseCase.findAll(searchRequest);
        return ResponseEntity.ok(result.map(mapper::toListDto));
    }

    /**
     * Gets a role by id.
     *
     * @param id role id
     * @return role details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDto(this.roleUseCase.findById(id)));
    }

    /**
     * Updates an existing role.
     *
     * @param id            role id
     * @param updateRoleDto request body
     * @return updated role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    public ResponseEntity<RoleDto> updateRole(@PathVariable UUID id, @RequestBody @Validated UpdateRoleDto updateRoleDto) {
        Role updated = this.roleUseCase.update(id, mapper.toUpdateCommand(updateRoleDto));
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    /**
     * Soft-deletes a role.
     *
     * @param id role id
     * @return empty response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        this.roleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Returns permissions grouped by entity for a role (e.g., USER -> [CREATE, READ]).
     *
     * @param id role id
     * @return grouped permissions
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<java.util.Map<String, java.util.Set<String>>> getRolePermissions(@PathVariable UUID id) {
        return ResponseEntity.ok(this.roleUseCase.findPermissionGroups(id));
    }

}
