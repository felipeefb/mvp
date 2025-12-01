package com.felipe.belo.mvp.user.dto;

import com.felipe.belo.mvp.role.dto.RoleListDto;
import jakarta.validation.constraints.Email;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.felipe.belo.mvp.user.entity.UserEntity}
 */
public record ListUserEntityDto(UUID id, String name, @Email String email, RoleListDto role) implements Serializable {
    @Serial
    private static final long serialVersionUID = 5220714431208772028L;
}