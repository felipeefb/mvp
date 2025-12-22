package com.felipe.belo.mvp.core.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pure domain model for users.
 */
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private UUID createdBy;
    private LocalDateTime createdDate;
    private UUID lastModifiedBy;
    private LocalDateTime lastModifiedDate;
    private UUID deletedBy;
    private LocalDateTime deletedAt;

    /** Creates an empty user. */
    public User() {
    }

    /**
     * Creates a user with the given parameters.
     *
     * @param id       user identifier
     * @param name     display name
     * @param email    e-mail address
     * @param password raw/encoded password
     * @param role     assigned role
     */
    public User(UUID id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Returns the user identifier.
     *
     * @return user id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Sets the user identifier.
     *
     * @param id user id
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Returns the user name.
     *
     * @return user name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user name.
     *
     * @param name display name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the e-mail address.
     *
     * @return e-mail address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the e-mail address.
     *
     * @param email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user password (encoded).
     *
     * @return user password (encoded)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user password.
     *
     * @param password raw or encoded password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the assigned role.
     *
     * @return assigned role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the assigned role.
     *
     * @param role role entity
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Returns the creator user id.
     *
     * @return creator user id
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
     * Returns the last modifier id.
     *
     * @return last modifier id
     */
    public UUID getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * Sets the last modifier id.
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
     * Returns the deleter id for soft delete.
     *
     * @return deleter id for soft delete
     */
    public UUID getDeletedBy() {
        return deletedBy;
    }

    /**
     * Sets the deleter id.
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
