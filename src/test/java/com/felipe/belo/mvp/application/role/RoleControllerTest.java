package com.felipe.belo.mvp.application.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.belo.mvp.core.component.MessageService;
import com.felipe.belo.mvp.core.exception.ApiExceptionHandler;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.application.role.controller.RoleController;
import com.felipe.belo.mvp.application.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.application.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDTO;
import com.felipe.belo.mvp.usecase.role.RoleUseCase;
import com.felipe.belo.mvp.usecase.role.command.CreateRoleCommand;
import com.felipe.belo.mvp.usecase.role.command.UpdateRoleCommand;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private StubRoleService roleService;
    private MessageService messageService;

    @BeforeEach
    void setup() {
        this.objectMapper = new ObjectMapper();
        this.messageService = new MessageServiceStub();
        this.roleService = new StubRoleService();
        RoleController controller = new RoleController(roleService);
        ApiExceptionHandler handler = new ApiExceptionHandler(messageService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void createRole_ReturnsCreated() throws Exception {
        Role response = new Role(UUID.randomUUID(), "Managers", EnumSet.of(Permissions.ROLE_LIST));
        roleService.createResponse = response;

        CreateRoleDto body = new CreateRoleDto("Managers", EnumSet.of(Permissions.ROLE_LIST));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/roles")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Managers"));
    }

    @Test
    void listRoles_ReturnsOk() throws Exception {
        Role item = new Role(UUID.randomUUID(), "A", EnumSet.of(Permissions.ROLE_LIST));
        roleService.pageResponse = new PageImpl<>(List.of(item), PageRequest.of(0, 20), 1);
        SearchRequestDTO request = new SearchRequestDTO(List.of(), 0, 20, null, false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/roles/search")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("A"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getRoleById_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        roleService.getByIdResponse = new Role(id, "Admins", EnumSet.of(Permissions.ROLE_READ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/roles/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admins"));
    }

    @Test
    void updateRole_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        roleService.updateResponse = new Role(id, "New", EnumSet.of(Permissions.ROLE_UPDATE));

        UpdateRoleDto body = new UpdateRoleDto("New", EnumSet.of(Permissions.ROLE_UPDATE));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/roles/" + id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New"));
    }

    @Test
    void deleteRole_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/roles/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void getRolePermissions_ReturnsGroupedPermissions() throws Exception {
        UUID id = UUID.randomUUID();
        roleService.permissionsResponse = java.util.Map.of(
                "USER", java.util.Set.of("READ", "LIST")
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/roles/" + id + "/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.USER").isArray())
                .andExpect(jsonPath("$.USER").value(org.hamcrest.Matchers.hasItem("READ")));
    }

    @Test
    void getRoleById_WhenNotFound_Returns404FromHandler() throws Exception {
        roleService.throwNotFound = true;
        UUID id = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/roles/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND));
    }
}

// Simple stub services to avoid Mockito/inline mocking issues on newer JDKs
class MessageServiceStub extends MessageService {
    MessageServiceStub() { super(null); }
    @Override public String getMessage(String code, Object[] args) { return code; }
    @Override public String getMessage(String code) { return code; }
}

class StubRoleService implements RoleUseCase {
    Role createResponse;
    Page<Role> pageResponse;
    Role getByIdResponse;
    Role updateResponse;
    boolean throwNotFound = false;
    java.util.Map<String, java.util.Set<String>> permissionsResponse = java.util.Map.of();

    StubRoleService() { }

    @Override
    public Role create(CreateRoleCommand command) {
        return createResponse;
    }

    @Override
    public Role update(UUID id, UpdateRoleCommand command) {
        return updateResponse;
    }

    @Override
    public Role findById(UUID id) {
        if (throwNotFound) throw new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND);
        return getByIdResponse;
    }

    @Override
    public void delete(UUID id) { /* no-op */ }

    @Override
    public org.springframework.data.domain.Page<Role> findAll(SearchRequestDTO searchRequest) {
        return pageResponse;
    }

    @Override
    public java.util.List<Role> findAll() {
        return java.util.List.of();
    }

    @Override
    public java.util.Map<String, java.util.Set<String>> findPermissionGroups(UUID id) {
        return permissionsResponse;
    }
}
