package com.felipe.belo.mvp.infra.user.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityMapperTest {

    @Test
    void allowedFields_AndAliases_AreConfigured() {
        UserEntityMapper mapper = Mappers.getMapper(UserEntityMapper.class);

        assertThat(mapper.allowedFields()).contains("id", "name", "email", "role.id", "deletedAt");
        assertThat(mapper.fieldAliases())
                .containsEntry("roleId", "role.id")
                .containsEntry("roleName", "role.name");
    }
}
