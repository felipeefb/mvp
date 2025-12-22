package com.felipe.belo.mvp.application.user.dto;

import com.felipe.belo.mvp.core.domain.model.User;
import jakarta.validation.constraints.Email;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for creating a {@link User}.
 *
 * @param name     the display name
 * @param email    the e-mail address
 * @param password the raw password to be encoded
 * @param role     the initial role
 */
public record CreateUserEntityDto(String name, @Email String email, String password,
                                  UserRoleDto role) implements Serializable {
    @Serial
    private static final long serialVersionUID = -9090313875376253082L;
}
