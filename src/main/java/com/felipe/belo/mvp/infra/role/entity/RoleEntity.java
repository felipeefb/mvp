package com.felipe.belo.mvp.infra.role.entity;

import com.felipe.belo.mvp.core.domain.entity.AudityEntity;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Role entity representing a group of permissions assigned to users.
 * Extends {@link AudityEntity} to include auditing fields
 * and uses a soft-delete strategy through {@code deletedAt}/{@code deletedBy}.
 */
@Entity
@Table(name = "role",
       uniqueConstraints = {@UniqueConstraint(name = "uk_role_name", columnNames = "name")})
@SQLRestriction("deleted_at is null")
public class RoleEntity extends AudityEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Set<Permissions> permissions = new HashSet<>();

    /**
     * JPA default constructor.
     */
    public RoleEntity() {
    }

    /**
     * Creates a new Role with the given state.
     *
     * @param name        the role name
     * @param permissions assigned permissions
     * @param deletedBy   user id who deleted the role, if soft-deleted
     * @param deletedAt   timestamp of soft deletion, if any
     */
    public RoleEntity(String name, Set<Permissions> permissions, UUID deletedBy, LocalDateTime deletedAt) {
        this.name = name;
        this.permissions = permissions;
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
    }

    /**
     * Gets the role identifier.
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
     * Gets the role name.
     *
     * @return role name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the permissions assigned to this role.
     *
     * @return permissions set
     */
    public Set<Permissions> getPermissions() {
        return permissions;
    }

    /**
     * Gets the user id who soft-deleted this role.
     *
     * @return deleter user id or null
     */
    public UUID getDeletedBy() {
        return deletedBy;
    }

    /**
     * Gets the soft-delete timestamp.
     *
     * @return deletion time or null
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
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
     * Sets the deleter user id for soft-delete.
     *
     * @param deletedBy deleter user id
     */
    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    /**
     * Sets the soft-delete timestamp.
     *
     * @param deletedAt deletion time
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    /**
     * Sets the permissions assigned to this role.
     *
     * @param permissions permissions set
     */
    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions;
    }
}
