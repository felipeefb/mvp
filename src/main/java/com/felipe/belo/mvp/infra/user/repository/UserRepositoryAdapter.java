package com.felipe.belo.mvp.infra.user.repository;

import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.infra.user.entity.UserEntity;
import com.felipe.belo.mvp.infra.user.mapper.UserEntityMapper;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userMapper = new UserEntityMapper();

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByNameIgnoreCase(String name) {
        return userJpaRepository.findByNameIgnoreCase(name).map(userMapper::toDomain);
    }

    @Override
    public Page<User> search(String search, boolean includeDeleted, Pageable pageable) {
        Page<UserEntity> page = userJpaRepository.search(search, includeDeleted, pageable);
        List<User> content = page.getContent().stream().map(userMapper::toDomain).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public User save(User user) {
        UserEntity entity;
        if (user.getId() != null) {
            entity = userJpaRepository.findById(user.getId()).orElseGet(UserEntity::new);
            userMapper.updateEntity(entity, user);
        } else {
            entity = userMapper.toEntity(user);
        }
        UserEntity saved = userJpaRepository.save(entity);
        return userMapper.toDomain(saved);
    }
}
