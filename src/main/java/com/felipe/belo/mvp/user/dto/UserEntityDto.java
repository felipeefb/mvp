package com.felipe.belo.mvp.user.dto;

import com.felipe.belo.mvp.role.dto.RoleDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.felipe.belo.mvp.user.entity.UserEntity}
 */
public record UserEntityDto(UUID createdBy, LocalDateTime createdDate, UUID lastModifiedBy,
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
                            RoleDto role, UUID deletedBy, LocalDateTime deletedAt) implements Serializable {
    @Serial
    private static final long serialVersionUID = 8954817447700268948L;
}