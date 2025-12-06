package com.felipe.belo.mvp.user.dto;

import com.felipe.belo.mvp.role.dto.RoleDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO for updating a {@link com.felipe.belo.mvp.user.entity.UserEntity}.
 *
 * @param name  the new user name
 * @param email the new e-mail address
 * @param role  the new role
 */
public record UpdateUserEntityDto( @NotBlank(message = "{I18nConstants.MESSAGE_USER_NAME_REQUIRED}")
                                   @Size(min = 5, max = 20, message = "{I18nConstants.MESSAGE_USER_NAME_LENGTH}")
                                   String name,
                                   @Email(message = "{I18nConstants.MESSAGE_USER_EMAIL_INVALID}")
                                   @NotBlank(message = "{I18nConstants.MESSAGE_USER_EMAIL_REQUIRED}")
                                   @Size(min = 5, max = 20, message = "{I18nConstants.MESSAGE_USER_EMAIL_LENGTH}")
                                   String email, RoleDto role) implements Serializable {
    @Serial
    private static final long serialVersionUID = 8860836577394346456L;
}