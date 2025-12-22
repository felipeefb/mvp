package com.felipe.belo.mvp.infra.role.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class RoleEntityMapperTest {

    @Test
    void allowedFields_AndAliases_AreConfigured() {
        RoleEntityMapper mapper = Mappers.getMapper(RoleEntityMapper.class);

        assertThat(mapper.allowedFields()).contains("id", "name", "createdDate", "deletedAt");
        assertThat(mapper.fieldAliases()).isEmpty();
    }
}
