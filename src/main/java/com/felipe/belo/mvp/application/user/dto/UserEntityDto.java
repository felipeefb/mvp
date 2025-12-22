package com.felipe.belo.mvp.application.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.felipe.belo.mvp.core.domain.model.User}.
 *
 * @param createdBy        the creator user id
 * @param createdDate      the creation timestamp
 * @param lastModifiedBy   the last modifier user e-mail
 * @param lastModifiedDate the last modification timestamp
 * @param id               the user id
 * @param name             the user name
 * @param email            the e-mail address
 * @param password         the hashed password
 * @param role             the role details
 * @param deletedBy        the user e-mail who soft-deleted this user, if any
 * @param deletedAt        the soft delete timestamp, if any
 */
public record UserEntityDto(UUID createdBy, LocalDateTime createdDate, String lastModifiedBy,
                            LocalDateTime lastModifiedDate, UUID id,
                            @NotBlank(message = "{I18nConstants.MESSAGE_USER_NAME_REQUIRED}")
                            @Size(min = 5, max = 20, message = "{I18nConstants.MESSAGE_USER_NAME_LENGTH}")
                            String name,
                            @Email(message = "{I18nConstants.MESSAGE_USER_EMAIL_INVALID}")
                            @NotBlank(message = "{I18nConstants.MESSAGE_USER_EMAIL_REQUIRED}")
                            @Size(min = 5, max = 20, message = "{I18nConstants.MESSAGE_USER_EMAIL_LENGTH}")
                            String email,

                            @NotBlank(message = "{I18nConstants.MESSAGE_USER_PASSWORD_REQUIRED}")
                            @Size(min = 5, max = 20, message = "{I18nConstants.MESSAGE_USER_PASSWORD_LENGTH}")
                            @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                                    message = "{I18nConstants.MESSAGE_USER_PASSWORD_PATTERN}")
                            String password,
                            UserRoleDto role, String deletedBy, LocalDateTime deletedAt) implements Serializable {
    @Serial
    private static final long serialVersionUID = 8954817447700268948L;
}
