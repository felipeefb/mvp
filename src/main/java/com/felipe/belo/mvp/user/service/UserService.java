package com.felipe.belo.mvp.user.service;

import com.felipe.belo.mvp.core.domain.page.dto.SearchFieldDTO;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import com.felipe.belo.mvp.core.service.CurrentUserService;
import com.felipe.belo.mvp.user.dto.CreateUserEntityDto;
import com.felipe.belo.mvp.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.user.dto.UserEntityDto;
import com.felipe.belo.mvp.user.dto.UserEntityListDto;
import com.felipe.belo.mvp.user.entity.UserEntity;
import com.felipe.belo.mvp.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntityDto create(@Valid CreateUserEntityDto createUserEntityDto) {
        return null;
    }

    public Page<UserEntityListDto> listUsers(SearchRequestDTO searchRequest) {
        Pageable pageable = PageRequest.of(
                Math.max(searchRequest.page(), 0),
                Math.max(searchRequest.size(), 1)
        );

        // Extract search term from filters if present
        String searchTerm = extractSearchTerm(searchRequest);

        Page<UserEntity> users = this.userRepository.search(
                searchTerm,
                searchRequest.includeDeleted(),
                pageable
        );

        // TODO: Map users to UserEntityListDto
        return null;
    }

    public UserEntityDto getUserById(UUID id) {
        return null;
    }

    public UserEntityDto update(UUID id, UpdateUserEntityDto updateUserEntityDto) {
        return null;
    }

    /**
     * Extracts the search term from the SearchRequestDTO filters.
     * Looks for filters on 'name' or 'email' fields with 'contains' operation.
     *
     * @param searchRequest the search request
     * @return the search term or null if not found
     */
    private String extractSearchTerm(SearchRequestDTO searchRequest) {
        if (searchRequest.filters() == null || searchRequest.filters().isEmpty()) {
            return null;
        }

        return searchRequest.filters().stream()
                .filter(filter -> ("name".equals(filter.field()) || "email".equals(filter.field()))
                        && ("contains".equalsIgnoreCase(filter.operation()) || ":".equals(filter.operation())))
                .map(SearchFieldDTO::value)
                .findFirst()
                .orElse(null);
    }

}