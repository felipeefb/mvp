package com.felipe.belo.mvp.core.domain.model;

import com.felipe.belo.mvp.core.utils.permissions.Permissions;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Pure domain model for roles.
 */
public class Role {
    private UUID id;
    private String name;
    private Set<Permissions> permissions = new HashSet<>();
    private UUID createdBy;
    private LocalDateTime createdDate;
    private UUID lastModifiedBy;
    private LocalDateTime lastModifiedDate;
    private UUID deletedBy;
    private LocalDateTime deletedAt;

    /** Creates an empty role. */
    public Role() {
    }

    /**
     * Creates a role with the given parameters.
     *
     * @param id          role identifier
     * @param name        role name
     * @param permissions granted permissions
     */
    public Role(UUID id, String name, Set<Permissions> permissions) {
        this.id = id;
        this.name = name;
        if (permissions != null) {
            this.permissions = new HashSet<>(permissions);
        }
    }

    /**
     * Returns the role identifier.
     *
     * @return role id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the role identifier.
     *
     * @param id role id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns the role name.
     *
     * @return role name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the role name.
     *
     * @param name role name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns granted permissions.
     *
     * @return granted permissions
     */
    public Set<Permissions> getPermissions() {
        return permissions;
    }

    /**
     * Sets granted permissions.
     *
     * @param permissions permissions set
     */
    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions == null ? new HashSet<>() : new HashSet<>(permissions);
    }

    /**
     * Returns the creator user id.
     *
     * @return user id that created the role
     */
    public UUID getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the creator user id.
     *
     * @param createdBy creator id
     */
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdDate creation date
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Returns the last modifier user id.
     *
     * @return last modifier user id
     */
    public UUID getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * Sets the last modifier user id.
     *
     * @param lastModifiedBy modifier id
     */
    public void setLastModifiedBy(UUID lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * Returns the last modification timestamp.
     *
     * @return last modification timestamp
     */
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Sets the last modification timestamp.
     *
     * @param lastModifiedDate modification date
     */
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Returns the user id that soft-deleted the role.
     *
     * @return user id that soft-deleted the role
     */
    public UUID getDeletedBy() {
        return deletedBy;
    }

    /**
     * Sets the soft-delete user id.
     *
     * @param deletedBy deleter id
     */
    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    /**
     * Returns the soft-delete timestamp.
     *
     * @return soft-delete timestamp
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * Sets the soft-delete timestamp.
     *
     * @param deletedAt delete timestamp
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
