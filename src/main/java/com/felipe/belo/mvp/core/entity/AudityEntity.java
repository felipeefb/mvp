package com.felipe.belo.mvp.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AudityEntity is an abstract base class designed to capture and automatically
 * manage entity audit information such as creation and modification metadata.
 * It uses JPA and Spring Data JPA auditing capabilities.
 *
 * This class is annotated with @MappedSuperclass, indicating that its
 * fields will be inherited by entity classes that extend it.
 *
 * The AuditingEntityListener is registered with this class to automatically
 * populate the audit fields using Spring Data JPA's auditing mechanism.
 *
 * Features:
 * - Tracks the user who created an entity via the 'createdBy' field.
 * - Tracks the timestamp when an entity was created via the 'createdDate' field.
 * - Tracks the user who last modified an entity via the 'lastModifiedBy' field.
 * - Tracks the timestamp when an entity was last modified via the 'lastModifiedDate' field.
 *
 * This class is intended to be extended by entity classes that require audit logging.
 *
 * Note:
 * The auditing annotations require Spring Data JPA's auditing feature to be enabled.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AudityEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private UUID lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public UUID getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(UUID lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}