package com.felipe.belo.mvp.user.mapper;

import com.felipe.belo.mvp.role.mapper.RoleMapper;
import com.felipe.belo.mvp.user.dto.ListUserEntityDto;
import com.felipe.belo.mvp.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.user.dto.UserEntityDto;
import com.felipe.belo.mvp.user.entity.CreateUserEntityDto;
import com.felipe.belo.mvp.user.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

/**
 * UserEntityMapper interface provides methods for mapping between UserEntity and its corresponding DTOs.
 * It utilizes MapStruct's mapping capabilities and is configured to use the SPRING component model.
 *
 * Mapping configurations:
 * - The unmapped target policy is set to IGNORE to suppress warnings for unmapped target properties.
 * - The nullValuePropertyMappingStrategy is set to IGNORE for partial updates to avoid overwriting existing values with null.
 * - The RoleMapper is used to handle the mapping of embedded Role objects.
 *
 * Methods:
 * - toEntity(CreateUserEntityDto createUserEntityDto): Maps from CreateUserEntityDto to UserEntity.
 * - toEntity(UserEntityDto userEntityDto): Maps from UserEntityDto to UserEntity.
 * - toEntity(UpdateUserEntityDto updateUserEntityDto): Maps from UpdateUserEntityDto to UserEntity.
 * - partialUpdate(UpdateUserEntityDto updateUserEntityDto, @MappingTarget UserEntity userEntity): Performs partial
 *   update by mapping UpdateUserEntityDto onto an existing UserEntity, ignoring null properties from the source.
 * - toDto(UserEntity userEntity): Maps from UserEntity to UserEntityDto.
 * - toDto(Iterable<UserEntity> entities): Maps a collection of UserEntity objects to a list of ListUserEntityDto.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
public interface UserEntityMapper {

    UserEntity toEntity(CreateUserEntityDto createUserEntityDto);
    UserEntity toEntity(UserEntityDto userEntityDto);
    UserEntity toEntity(UpdateUserEntityDto updateUserEntityDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserEntity partialUpdate(UpdateUserEntityDto updateUserEntityDto, @MappingTarget UserEntity userEntity);
    UserEntityDto toDto(UserEntity userEntity);
    List<ListUserEntityDto> toDto(Iterable<UserEntity> entities);
}