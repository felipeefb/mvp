package com.felipe.belo.mvp.infra.role.mapper;

import com.felipe.belo.mvp.core.domain.SearchFieldMapper;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.infra.role.entity.RoleEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MapStruct mapper between {@link RoleEntity} persistence model and {@link Role} domain model.
 * Also exposes allowed search fields/aliases for dynamic filtering.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleEntityMapper extends SearchFieldMapper {

    /**
     * Converts a JPA entity to a domain role.
     *
     * @param entity source entity
     * @return domain role
     */
    Role toDomain(RoleEntity entity);

    /**
     * Converts a domain role to a JPA entity.
     *
     * @param role source domain model
     * @return entity
     */
    RoleEntity toEntity(Role role);

    /**
     * Updates an existing entity with non-null fields from the source.
     *
     * @param target entity to update
     * @param source source domain data
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget RoleEntity target, Role source);

    /**
     * Converts a list of entities to domain roles.
     *
     * @param entities source list
     * @return list of domain roles
     */
    List<Role> toDomainList(List<RoleEntity> entities);

    @Override
    default Set<String> allowedFields() {
        return Set.of(
                "id",
                "name",
                "createdDate",
                "lastModifiedDate",
                "deletedAt",
                "deletedBy"
        );
    }

    @Override
    default Map<String, String> fieldAliases() {
        return Map.of();
    }
}
