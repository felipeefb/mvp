package com.felipe.belo.mvp.usecase.role;

import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import com.felipe.belo.mvp.usecase.role.command.CreateRoleCommand;
import com.felipe.belo.mvp.usecase.role.command.UpdateRoleCommand;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RoleUseCase {
    Role create(CreateRoleCommand command);

    Role update(UUID id, UpdateRoleCommand command);

    Role findById(UUID id);

    void delete(UUID id);

    List<Role> findAll();

    Page<Role> findAll(SearchRequestDTO searchRequest);

    java.util.Map<String, java.util.Set<String>> findPermissionGroups(UUID id);
}
