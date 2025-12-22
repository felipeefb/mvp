package com.felipe.belo.mvp.application.user.dto;

import jakarta.validation.constraints.Email;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight DTO for listing {@link com.felipe.belo.mvp.core.domain.model.User} entries.
 *
 * @param id        the user identifier
 * @param name      the display name
 * @param email     the e-mail address
 * @param role      the role summary
 * @param deletedBy user e-mail who soft-deleted the user, if any
 * @param deletedAt soft-delete timestamp, if any
 */
public record UserEntityListDto(UUID id, String name, @Email String email, UserRoleListDto role, String deletedBy,
                                LocalDateTime deletedAt) implements Serializable {
    @Serial
    private static final long serialVersionUID = 5220714431208772028L;
}
