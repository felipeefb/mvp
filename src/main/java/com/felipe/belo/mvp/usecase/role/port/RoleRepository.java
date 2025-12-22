package com.felipe.belo.mvp.usecase.role.port;

import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence abstraction for roles.
 */
public interface RoleRepository {
    /**
     * Finds a role by id.
     *
     * @param id role id
     * @return optional role
     */
    Optional<Role> findById(UUID id);

    /**
     * Finds a role by normalized name.
     *
     * @param name normalized name
     * @return optional role
     */
    Optional<Role> findByNormalizedName(String name);

    /**
     * Lists active (non-deleted) roles.
     *
     * @return list of roles
     */
    List<Role> findAllByDeletedAtIsNull();

    /**
     * Searches roles with filters/pagination.
     *
     * @param request search request
     * @param pageable paging options
     * @return paged roles
     */
    Page<Role> search(SearchRequestDto request, Pageable pageable);

    /**
     * Persists a role.
     *
     * @param role role to save
     * @return saved role
     */
    Role save(Role role);

}
