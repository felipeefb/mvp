package com.felipe.belo.mvp.application.permissions.controller;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Exposes grouped permission metadata.
 */
@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permissions", description = I18nConstants.SWAGGER_PERMISSIONS_TAG)
public class PermissionsController {

    /** Default constructor for Spring. */
    public PermissionsController() { }

    /**
     * Lists all permissions grouped by entity prefix.
     *
     * @return grouped permissions map
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    @Operation(
            summary = I18nConstants.SWAGGER_PERMISSIONS_LIST_SUMMARY,
            description = I18nConstants.SWAGGER_PERMISSIONS_LIST_DESC
    )
    public ResponseEntity<Map<String, Set<String>>> listGroupedPermissions() {
        Map<String, Set<String>> grouped = new LinkedHashMap<>();
        for (Permissions permission : Permissions.values()) {
            String[] parts = permission.name().split("_", 2);
            String group = parts[0];
            String action = parts.length > 1 ? parts[1] : permission.name();
            grouped.computeIfAbsent(group, k -> new LinkedHashSet<>()).add(action);
        }
        return ResponseEntity.ok(grouped);
    }
}