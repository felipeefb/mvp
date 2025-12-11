package com.felipe.belo.mvp.user.mapper;

import com.felipe.belo.mvp.role.mapper.RoleMapper;
import com.felipe.belo.mvp.user.dto.UserEntityListDto;
import com.felipe.belo.mvp.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.user.dto.UserEntityDto;
import com.felipe.belo.mvp.user.dto.CreateUserEntityDto;
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
 * - {@code toDto(Iterable<UserEntity> entities)}: Maps a collection of {@code UserEntity} objects to a list of {@code ListUserEntityDto}.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
public interface UserEntityMapper {

    /**
     * Maps a create DTO to a {@link UserEntity}.
     * @param createUserEntityDto source
     * @return new entity
     */
    UserEntity toEntity(CreateUserEntityDto createUserEntityDto);

    /**
     * Maps a detailed DTO to a {@link UserEntity}.
     * @param userEntityDto source
     * @return entity
     */
    UserEntity toEntity(UserEntityDto userEntityDto);

    /**
     * Maps an update DTO to a {@link UserEntity}.
     * @param updateUserEntityDto source
     * @return entity
     */
    UserEntity toEntity(UpdateUserEntityDto updateUserEntityDto);

    /**
     * Partially updates an entity from an update DTO, ignoring nulls.
     * @param updateUserEntityDto source changes
     * @param userEntity target entity
     * @return the updated entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserEntity partialUpdate(UpdateUserEntityDto updateUserEntityDto, @MappingTarget UserEntity userEntity);

    /**
     * Maps an entity to a detailed DTO.
     * @param userEntity source
     * @return dto
     */
    UserEntityDto toDto(UserEntity userEntity);

    /**
     * Maps a collection of entities to lightweight list DTOs.
     * @param entities source iterable
     * @return list of list DTOs
     */
    List<UserEntityListDto> toDto(Iterable<UserEntity> entities);
}