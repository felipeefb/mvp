package com.felipe.belo.mvp.role.entity;

import com.felipe.belo.mvp.core.entity.AudityEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import com.felipe.belo.mvp.utils.permissions.Permissions;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "role",
       uniqueConstraints = {@UniqueConstraint(name = "uk_role_name", columnNames = "name")})
public class Role extends AudityEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permission", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private Set<Permissions> permissions = new HashSet<>();

    public Role() {
    }

    public Role(String name, Set<Permissions> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Set<Permissions> getPermissions() {
        return permissions;
    }
}