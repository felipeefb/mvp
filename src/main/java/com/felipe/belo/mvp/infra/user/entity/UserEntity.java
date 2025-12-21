package com.felipe.belo.mvp.infra.user.entity;

import com.felipe.belo.mvp.core.domain.entity.AudityEntity;
import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User entity representing application users.
 * Extends {@link AudityEntity} to include auditing fields
 * and uses soft-delete fields {@code deletedAt}/{@code deletedBy}.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")
        }
)
@SQLRestriction("deleted_at is null")
public class UserEntity extends AudityEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @GeneratedValue
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_users_roles"))
    private RoleEntity role;

    private UUID deletedBy;

    private LocalDateTime deletedAt;

    /**
     * JPA default constructor.
     */
    public UserEntity() {
    }

    /**
     * Full constructor for convenience/testing.
     *
     * @param id id
     * @param name name
     * @param email email
     * @param password password
     * @param role role
     * @param deletedBy soft-delete user id
     * @param deletedAt soft-delete timestamp
     */
    public UserEntity(UUID id, String name, String email, String password, RoleEntity role, UUID deletedBy, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
    }

    /**
     * Gets the user id.
     * @return id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the display name.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the e-mail address.
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the hashed password.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the role.
     * @return role
     */
    public RoleEntity getRole() {
        return role;
    }

    /**
     * Gets the user id that performed soft-delete.
     * @return deleter id or null
     */
    public UUID getDeletedBy() {
        return deletedBy;
    }

    /**
     * Gets the soft-delete timestamp.
     * @return deletion time or null
     */
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    /**
     * Sets the display name.
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the e-mail address.
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the hashed password.
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the role.
     * @param role role
     */
    public void setRole(RoleEntity role) {
        this.role = role;
    }

    /**
     * Sets the user id that performed soft-delete.
     * @param deletedBy deleter id
     */
    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    /**
     * Sets the soft-delete timestamp.
     * @param deletedAt deletion time
     */
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
