package com.felipe.belo.mvp.user.entity;

import com.felipe.belo.mvp.role.dto.RoleDto;
import jakarta.validation.constraints.Email;

import java.io.Serializable;

/**
 * DTO for {@link UserEntity}
 */
public record CreateUserEntityDto(String name, @Email String email, String password,
                                  RoleDto role) implements Serializable {
    private static final long serialVersionUID = -9090313875376253082L;
}