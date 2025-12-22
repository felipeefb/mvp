package com.felipe.belo.mvp.application.user.controller;

import java.util.UUID;

import com.felipe.belo.mvp.application.user.dto.UserEntityListDto;
import com.felipe.belo.mvp.application.user.mapper.UserDtoMapper;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import com.felipe.belo.mvp.usecase.user.CreateUserUseCase;
import com.felipe.belo.mvp.usecase.user.GetUserByIdUseCase;
import com.felipe.belo.mvp.usecase.user.SearchUsersUseCase;
import com.felipe.belo.mvp.usecase.user.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * REST controller for user endpoints.
 * Keeps DTO mapping at the edge, delegating domain logic to use cases.
 */
@RestController
@RequestMapping("/api/v1/users")
@Validated
@Tag(name = "Users", description = I18nConstants.SWAGGER_USER_TAG)
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final SearchUsersUseCase searchUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UserDtoMapper mapper;

    /**
     * Creates a new controller with required dependencies.
     *
     * @param createUserUseCase use case to create users
     * @param searchUsersUseCase use case to search users
     * @param getUserByIdUseCase use case to fetch a user by id
     * @param updateUserUseCase use case to update users
     * @param mapper mapper for DTO/domain conversions
     */
    public UserController(CreateUserUseCase createUserUseCase,
                          SearchUsersUseCase searchUsersUseCase,
                          GetUserByIdUseCase getUserByIdUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          UserDtoMapper mapper) {
        this.createUserUseCase = createUserUseCase;
        this.searchUsersUseCase = searchUsersUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.mapper = mapper;
    }

    /**
     * Creates a new user.
     *
     * @param createUserEntityDto request payload
     * @return created DTO
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @Operation(
            summary = I18nConstants.SWAGGER_USER_CREATE_SUMMARY,
            description = I18nConstants.SWAGGER_USER_CREATE_DESC
    )
    public ResponseEntity<CreateUserEntityDto> createUser(@Valid @RequestBody CreateUserEntityDto createUserEntityDto) {
        User created = createUserUseCase.create(mapper.toCreateCommand(createUserEntityDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCreateDto(created));
    }

    /**
     * Lists users with pagination and filtering.
     *
     * @param searchRequest search and paging parameters
     * @return paged users
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER_LIST')")
    @Operation(
            summary = I18nConstants.SWAGGER_USER_LIST_SUMMARY,
            description = I18nConstants.SWAGGER_USER_LIST_DESC
    )
    public ResponseEntity<Page<UserEntityListDto>> listUsers(@ModelAttribute @Validated SearchRequestDto searchRequest) {
        Page<User> users = searchUsersUseCase.search(searchRequest);
        return ResponseEntity.ok(users.map(mapper::toListDto));
    }

    /**
     * Retrieves a user by id.
     *
     * @param id user identifier
     * @return user DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    @Operation(
            summary = I18nConstants.SWAGGER_USER_GET_SUMMARY,
            description = I18nConstants.SWAGGER_USER_GET_DESC
    )
    public ResponseEntity<UserEntityDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toDto(getUserByIdUseCase.findById(id)));
    }

    /**
     * Updates an existing user.
     *
     * @param id user id
     * @param updateUserEntityDto update payload
     * @return updated DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Operation(
            summary = I18nConstants.SWAGGER_USER_UPDATE_SUMMARY,
            description = I18nConstants.SWAGGER_USER_UPDATE_DESC
    )
    public ResponseEntity<UpdateUserEntityDto> update (@PathVariable UUID id, @RequestBody @Validated UpdateUserEntityDto updateUserEntityDto) {
        User updated = updateUserUseCase.update(id, mapper.toUpdateCommand(updateUserEntityDto));
        return ResponseEntity.ok(mapper.toUpdateDto(updated));
    }

}
