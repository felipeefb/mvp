package com.felipe.belo.mvp.user.controller;

import java.util.UUID;

import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import com.felipe.belo.mvp.user.dto.UserEntityListDto;
import com.felipe.belo.mvp.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.felipe.belo.mvp.user.dto.CreateUserEntityDto;
import com.felipe.belo.mvp.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.user.dto.UserEntityDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<UserEntityDto> createUser(@Valid @RequestBody CreateUserEntityDto createUserEntityDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(createUserEntityDto));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAuthority('USER_LIST')")
    public ResponseEntity<Page<UserEntityListDto>> listUsers(@RequestBody @Validated SearchRequestDTO searchRequest) {
        return ResponseEntity.ok(userService.listUsers(searchRequest));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<UserEntityDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserEntityDto> update (@PathVariable UUID id, @RequestBody @Validated UpdateUserEntityDto updateUserEntityDto) {
        return ResponseEntity.ok(userService.update(id, updateUserEntityDto));
    }

}