package com.felipe.belo.mvp.application.role.controller;

import com.felipe.belo.mvp.application.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleDto;
import com.felipe.belo.mvp.application.role.dto.RoleListDto;
import com.felipe.belo.mvp.application.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.application.role.mapper.RoleDtoMapper;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import com.felipe.belo.mvp.usecase.role.RoleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing roles.
 * Maps HTTP request/response DTOs to domain use cases at the application boundary.
 */
@RestController
@RequestMapping("/api/v1/roles")
@Validated
@Tag(name = "Roles", description = I18nConstants.SWAGGER_ROLE_TAG)
public class RoleController {

    private final RoleUseCase roleUseCase;
    private final RoleDtoMapper mapper;

    /**
     * Creates a new controller instance.
     *
     * @param roleUseCase the role use case
     * @param mapper mapper for translating role DTOs
     */
    public RoleController(RoleUseCase roleUseCase, RoleDtoMapper mapper) {
        this.roleUseCase = roleUseCase;
        this.mapper = mapper;
    }

    /**
     * Creates a new role.
     *
     * @param roleDto request body
     * @return the created role
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @Operation(
            summary = I18nConstants.SWAGGER_ROLE_CREATE_SUMMARY,
            description = I18nConstants.SWAGGER_ROLE_CREATE_DESC
    )
    public ResponseEntity<CreateRoleDto> createRole(@RequestBody @Validated CreateRoleDto roleDto) {
        Role created = this.roleUseCase.create(mapper.toCreateCommand(roleDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCreateDto(created));
    }

    /**
     * Lists roles with search, pagination, and filtering based on the provided search request.
     * @param searchRequest search request containing filters, pagination, and soft-delete inclusion
     * @return a paginated list of roles
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_LIST')")
    @Operation(
            summary = I18nConstants.SWAGGER_ROLE_LIST_SUMMARY,
            description = I18nConstants.SWAGGER_ROLE_LIST_DESC
    )
    public ResponseEntity<Page<RoleListDto>> listRoles(@ModelAttribute @Validated SearchRequestDto searchRequest) {
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
    @Operation(
            summary = I18nConstants.SWAGGER_ROLE_GET_SUMMARY,
            description = I18nConstants.SWAGGER_ROLE_GET_DESC
    )
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
    @Operation(
            summary = I18nConstants.SWAGGER_ROLE_UPDATE_SUMMARY,
            description = I18nConstants.SWAGGER_ROLE_UPDATE_DESC
    )
    public ResponseEntity<UpdateRoleDto> updateRole(@PathVariable UUID id, @RequestBody @Validated UpdateRoleDto updateRoleDto) {
        Role updated = this.roleUseCase.update(id, mapper.toUpdateCommand(updateRoleDto));
        return ResponseEntity.ok(mapper.toUpdateDto(updated));
    }

    /**
     * Soft-deletes a role.
     *
     * @param id role id
     * @return empty response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @Operation(
            summary = I18nConstants.SWAGGER_ROLE_DELETE_SUMMARY,
            description = I18nConstants.SWAGGER_ROLE_DELETE_DESC
    )
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        this.roleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}