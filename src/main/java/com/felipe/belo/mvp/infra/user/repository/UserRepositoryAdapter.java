package com.felipe.belo.mvp.infra.user.repository;

import com.felipe.belo.mvp.core.domain.SearchSpecification;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import com.felipe.belo.mvp.infra.user.entity.UserEntity;
import com.felipe.belo.mvp.infra.user.mapper.UserEntityMapper;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA-backed implementation of the user repository port.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {
    private static final UserEntityMapper USER_MAPPER = Mappers.getMapper(UserEntityMapper.class);
    private final UserJpaRepository userJpaRepository;

    /**
     * Creates a new adapter.
     *
     * @param userJpaRepository JPA repository
     */
    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(USER_MAPPER::toDomain);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email).map(USER_MAPPER::toDomain);
    }

    @Override
    public Optional<User> findByNameIgnoreCase(String name) {
        return userJpaRepository.findByNameIgnoreCase(name).map(USER_MAPPER::toDomain);
    }

    @Override
    public Page<User> search(SearchRequestDto request, Pageable pageable) {
        Page<UserEntity> page = userJpaRepository.findAll(SearchSpecification.build(request, USER_MAPPER), pageable);
        List<User> content = page.getContent().stream().map(USER_MAPPER::toDomain).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public User save(User user) {
        UserEntity entity;
        if (user.getId() != null) {
            entity = userJpaRepository.findById(user.getId()).orElseGet(UserEntity::new);
            USER_MAPPER.updateEntity(entity, user);
        } else {
            entity = USER_MAPPER.toEntity(user);
        }
        UserEntity saved = userJpaRepository.save(entity);
        return USER_MAPPER.toDomain(saved);
    }
}
