package com.felipe.belo.mvp.role.controller;

import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import com.felipe.belo.mvp.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.role.dto.RoleDto;
import com.felipe.belo.mvp.role.dto.RoleListDto;
import com.felipe.belo.mvp.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.role.service.RoleService;
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

    private final RoleService roleService;

    /**
     * Creates a new controller instance.
     *
     * @param roleService the role service
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(roleDto));
    }

    /**
     * Lists roles with search, pagination, and filtering based on the provided search request.
     * @param searchRequest search request containing filters, pagination, and soft-delete inclusion
     * @return a paginated list of roles
     */
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_LIST')")
    public ResponseEntity<Page<RoleListDto>> listRoles(@RequestBody @Validated SearchRequestDTO searchRequest) {
        return ResponseEntity.ok(this.roleService.findAll(searchRequest));
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
        return ResponseEntity.ok(this.roleService.findById(id));
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
        return ResponseEntity.ok(this.roleService.update(id, updateRoleDto));
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
        this.roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}