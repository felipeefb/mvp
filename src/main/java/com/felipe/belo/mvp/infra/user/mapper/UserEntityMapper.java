package com.felipe.belo.mvp.infra.user.mapper;

import com.felipe.belo.mvp.core.domain.SearchFieldMapper;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import com.felipe.belo.mvp.infra.role.mapper.RoleEntityMapper;
import com.felipe.belo.mvp.infra.user.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Map;
import java.util.Set;

/**
 * MapStruct mapper between {@link UserEntity} persistence model and {@link User} domain model.
 * Provides allowed field metadata for dynamic filtering.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = RoleEntityMapper.class)
public interface UserEntityMapper extends SearchFieldMapper {

    /**
     * Converts an entity to a domain user.
     *
     * @param entity source JPA entity
     * @return domain user
     */
    User toDomain(UserEntity entity);

    /**
     * Converts a domain user to an entity.
     *
     * @param user source domain model
     * @return entity
     */
    UserEntity toEntity(User user);

    /**
     * Updates an entity ignoring nulls.
     *
     * @param target entity to update
     * @param source source domain data
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget UserEntity target, User source);

    @Override
    default Set<String> allowedFields() {
        return Set.of(
                "id",
                "name",
                "email",
                "role.id",
                "role.name",
                "createdDate",
                "lastModifiedDate",
                "deletedAt",
                "deletedBy"
        );
    }

    @Override
    default Map<String, String> fieldAliases() {
        return Map.of(
                "roleId", "role.id",
                "roleName", "role.name"
        );
    }
}
