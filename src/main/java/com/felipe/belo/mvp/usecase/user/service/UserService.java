package com.felipe.belo.mvp.usecase.user.service;

import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import com.felipe.belo.mvp.core.domain.page.PageableUtils;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import com.felipe.belo.mvp.usecase.user.CreateUserUseCase;
import com.felipe.belo.mvp.usecase.user.FindUserByEmailUseCase;
import com.felipe.belo.mvp.usecase.user.FindUserByUsernameUseCase;
import com.felipe.belo.mvp.usecase.user.GetUserByIdUseCase;
import com.felipe.belo.mvp.usecase.user.SearchUsersUseCase;
import com.felipe.belo.mvp.usecase.user.UpdateUserUseCase;
import com.felipe.belo.mvp.usecase.user.LookupUserByIdUseCase;
import com.felipe.belo.mvp.usecase.user.command.CreateUserCommand;
import com.felipe.belo.mvp.usecase.user.command.UpdateUserCommand;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import com.felipe.belo.mvp.usecase.role.port.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service layer implementing user use cases.
 */
@Service
public class UserService implements CreateUserUseCase, FindUserByEmailUseCase, FindUserByUsernameUseCase,
        GetUserByIdUseCase, SearchUsersUseCase, UpdateUserUseCase, LookupUserByIdUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user service.
     *
     * @param userRepository repository for users
     * @param roleRepository repository for roles
     * @param passwordEncoder password encoder
     */
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(CreateUserCommand command) {
        Optional<User> existing = userRepository.findByEmailIgnoreCase(command.email());
        if (existing.isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, I18nConstants.MESSAGE_USER_EMAIL_EXISTS);
        }

        if (command.roleId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND);
        }

        Role role = roleRepository.findById(command.roleId())
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND));

        User user = new User();
        user.setName(command.name());
        user.setEmail(command.email());
        user.setPassword(passwordEncoder.encode(command.password()));
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByNameIgnoreCase(username);
    }

    @Override
    public Page<User> search(SearchRequestDto searchRequest) {
        Pageable pageable = PageableUtils.from(searchRequest);
        return this.userRepository.search(searchRequest, pageable);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_USER_NOT_FOUND));
    }

    @Override
    public Optional<User> findOptionalById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User update(UUID id, UpdateUserCommand command) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_USER_NOT_FOUND));

        if (command.name() != null) {
            existing.setName(command.name());
        }
        if (command.email() != null) {
            existing.setEmail(command.email());
        }
        if (command.password() != null) {
            existing.setPassword(passwordEncoder.encode(command.password()));
        }
        if (command.roleId() != null) {
            Role role = roleRepository.findById(command.roleId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND));
            existing.setRole(role);
        }
        return userRepository.save(existing);
    }

}
