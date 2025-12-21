package com.felipe.belo.mvp.application.user.controller;

import java.util.UUID;

import com.felipe.belo.mvp.application.user.dto.UserEntityListDto;
import com.felipe.belo.mvp.application.user.mapper.UserDtoMapper;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import com.felipe.belo.mvp.usecase.user.CreateUserUseCase;
import com.felipe.belo.mvp.usecase.user.GetUserByIdUseCase;
import com.felipe.belo.mvp.usecase.user.SearchUsersUseCase;
import com.felipe.belo.mvp.usecase.user.UpdateUserUseCase;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.felipe.belo.mvp.application.user.dto.CreateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UserEntityDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final SearchUsersUseCase searchUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UserDtoMapper mapper = new UserDtoMapper();

    public UserController(CreateUserUseCase createUserUseCase,
                          SearchUsersUseCase searchUsersUseCase,
                          GetUserByIdUseCase getUserByIdUseCase,
                          UpdateUserUseCase updateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.searchUsersUseCase = searchUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserEntityDto> createUser(@Valid @RequestBody CreateUserEntityDto createUserEntityDto) {
        User created = createUserUseCase.create(mapper.toCreateCommand(createUserEntityDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('USER_LIST')")
    public ResponseEntity<Page<UserEntityListDto>> listUsers(@RequestBody @Validated SearchRequestDTO searchRequest) {
        Page<User> users = searchUsersUseCase.search(searchRequest);
        return ResponseEntity.ok(users.map(mapper::toListDto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserEntityDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDto(getUserByIdUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserEntityDto> update (@PathVariable UUID id, @RequestBody @Validated UpdateUserEntityDto updateUserEntityDto) {
        User updated = updateUserUseCase.update(id, mapper.toUpdateCommand(updateUserEntityDto));
        return ResponseEntity.ok(mapper.toDto(updated));
    }

}
