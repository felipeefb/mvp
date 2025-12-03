package com.felipe.belo.mvp.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.belo.mvp.core.component.MessageService;
import com.felipe.belo.mvp.core.exception.ApiExceptionHandler;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.role.controller.RoleController;
import com.felipe.belo.mvp.role.dto.CreateRoleDto;
import com.felipe.belo.mvp.role.dto.RoleDto;
import com.felipe.belo.mvp.role.dto.RoleListDto;
import com.felipe.belo.mvp.role.dto.UpdateRoleDto;
import com.felipe.belo.mvp.role.service.RoleService;
import com.felipe.belo.mvp.utils.I18nConstants;
import com.felipe.belo.mvp.utils.permissions.Permissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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
        RoleDto response = new RoleDto(null, null, null, null, UUID.randomUUID(), "Managers", EnumSet.of(Permissions.ROLE_LIST));
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
        roleService.listResponse = List.of(new RoleListDto(UUID.randomUUID(), "A", EnumSet.of(Permissions.ROLE_LIST)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("A"));
    }

    @Test
    void getRoleById_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        roleService.getByIdResponse = new RoleDto(null, null, null, null, id, "Admins", EnumSet.of(Permissions.ROLE_READ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/roles/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admins"));
    }

    @Test
    void updateRole_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        roleService.updateResponse = new RoleDto(null, null, null, null, id, "New", EnumSet.of(Permissions.ROLE_UPDATE));

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

class StubRoleService extends RoleService {
    RoleDto createResponse;
    List<RoleListDto> listResponse;
    RoleDto getByIdResponse;
    RoleDto updateResponse;
    boolean throwNotFound = false;

    StubRoleService() { super(null, null, null); }

    @Override
    public RoleDto create(CreateRoleDto createRoleDto) {
        return createResponse;
    }

    @Override
    public RoleDto update(UUID id, UpdateRoleDto updateRoleDto) {
        return updateResponse;
    }

    @Override
    public RoleDto findById(UUID id) {
        if (throwNotFound) throw new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_ROLE_NAME_NOT_FOUND);
        return getByIdResponse;
    }

    @Override
    public void delete(UUID id) { /* no-op */ }

    @Override
    public List<RoleListDto> findAll() {
        return listResponse;
    }
}