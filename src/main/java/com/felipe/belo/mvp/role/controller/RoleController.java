package com.felipe.belo.mvp.role.controller;

import com.felipe.belo.mvp.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.role.dto.RoleDto;
import com.felipe.belo.mvp.role.dto.RoleListDto;
import com.felipe.belo.mvp.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.role.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@Validated
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody @Validated CreateRoleDto roleDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(roleDto));
    }

    @GetMapping
    public ResponseEntity<List<RoleListDto>> listRoles() {
        return ResponseEntity.ok(this.roleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(this.roleService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable UUID id, @RequestBody @Validated UpdateRoleDto updateRoleDto) {
        return ResponseEntity.ok(this.roleService.update(id, updateRoleDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        this.roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}