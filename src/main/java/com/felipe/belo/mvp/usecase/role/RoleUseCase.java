package com.felipe.belo.mvp.usecase.role;

import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import com.felipe.belo.mvp.usecase.role.command.CreateRoleCommand;
import com.felipe.belo.mvp.usecase.role.command.UpdateRoleCommand;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Facade for role-related operations.
 */
public interface RoleUseCase {
    /**
     * Creates a role.
     *
     * @param command create command
     * @return created role
     */
    Role create(CreateRoleCommand command);

    /**
     * Updates a role by id.
     *
     * @param id      role id
     * @param command update command
     * @return updated role
     */
    Role update(UUID id, UpdateRoleCommand command);

    /**
     * Finds a role by id.
     *
     * @param id role id
     * @return role
     */
    Role findById(UUID id);

    /**
     * Soft-deletes a role by id.
     *
     * @param id role id
     */
    void delete(UUID id);

    /**
     * Lists active roles.
     *
     * @return list of roles
     */
    List<Role> findAll();

    /**
     * Searches roles with filters/pagination.
     *
     * @param searchRequest search criteria
     * @return paged roles
     */
    Page<Role> findAll(SearchRequestDto searchRequest);

}
