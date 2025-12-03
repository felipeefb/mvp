package com.felipe.belo.mvp.role.entity;

import com.felipe.belo.mvp.core.entity.AudityEntity;
import com.felipe.belo.mvp.utils.permissions.Permissions;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "role",
       uniqueConstraints = {@UniqueConstraint(name = "uk_role_name", columnNames = "name")})
@SQLRestriction("deleted_at is null")
public class Role extends AudityEntity {

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

    public Role() {
    }

    public Role(String name, Set<Permissions> permissions, UUID deletedBy, LocalDateTime deletedAt) {
        this.name = name;
        this.permissions = permissions;
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions;
    }
}