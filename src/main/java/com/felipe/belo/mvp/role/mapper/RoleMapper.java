package com.felipe.belo.mvp.role.mapper;

import com.felipe.belo.mvp.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.role.dto.RoleDto;
import com.felipe.belo.mvp.role.dto.RoleListDto;
import com.felipe.belo.mvp.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.role.entity.Role;
import com.felipe.belo.mvp.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.user.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * RoleMapper interface provides methods for mapping between Role entity and its corresponding DTOs.
 * It utilizes MapStruct's mapping capabilities and is configured to use the SPRING component model.
 *
 * Mapping configurations:
 * - The unmapped target policy is set to IGNORE to suppress warnings for unmapped target properties.
 * - The nullValuePropertyMappingStrategy is set to IGNORE for partial updates to avoid overwriting existing values with null.
 *
 * Methods:
 * - toEntity(CreateRoleDto dto): Maps from CreateRoleDto to Role entity.
 * - toEntity(UpdateRoleDto dto): Maps from UpdateRoleDto to Role entity.
 * - toEntity(RoleDto dto): Maps from RoleDto to Role entity.
 * - partialUpdate(UpdateRoleDto updateRoleDto, @MappingTarget Role role): Performs partial update by mapping
 *   UpdateRoleDto onto an existing Role entity, ignoring null properties from the source.
 * - updateEntityFromDto(UpdateRoleDto dto, @MappingTarget Role entity): Updates an existing Role entity
 *   with the properties from UpdateRoleDto, ignoring null properties from the source.
 * - toDto(Role entity): Maps from Role entity to RoleDto.
 * - toDto(Iterable<Role> entities): Maps a collection of Role entities to a list of RoleListDto.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    Role toEntity(CreateRoleDto dto);
    Role toEntity(UpdateRoleDto dto);
    Role toEntity(RoleDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Role partialUpdate(UpdateRoleDto updateRoleDto, @MappingTarget Role role);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateRoleDto dto, @MappingTarget Role entity);
    RoleDto toDto(Role entity);
   List<RoleListDto> toDtoList(Iterable<Role> entities);
}