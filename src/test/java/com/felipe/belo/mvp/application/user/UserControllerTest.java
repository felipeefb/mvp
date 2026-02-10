package com.felipe.belo.mvp.application.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.belo.mvp.application.shared.api.UserEmailResolver;
import com.felipe.belo.mvp.application.user.controller.UserController;
import com.felipe.belo.mvp.application.user.dto.CreateUserEntityDto;
import com.felipe.belo.mvp.application.user.dto.UpdateUserEntityDto;
import com.felipe.belo.mvp.application.user.mapper.UserDtoMapper;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.domain.page.request.SearchRequestDto;
import com.felipe.belo.mvp.core.exception.ApiExceptionHandler;
import com.felipe.belo.mvp.core.component.MessageService;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.utils.I18nConstants;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import com.felipe.belo.mvp.usecase.user.CreateUserUseCase;
import com.felipe.belo.mvp.usecase.user.GetUserByIdUseCase;
import com.felipe.belo.mvp.usecase.user.SearchUsersUseCase;
import com.felipe.belo.mvp.usecase.user.UpdateUserUseCase;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private StubCreate createUseCase;
    private StubSearch searchUseCase;
    private StubGet getUseCase;
    private StubUpdate updateUseCase;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        createUseCase = new StubCreate();
        searchUseCase = new StubSearch();
        getUseCase = new StubGet();
        updateUseCase = new StubUpdate();

        UserDtoMapper userDtoMapper = new UserDtoMapper(new UserEmailResolver(id -> Optional.empty()));
        UserController controller = new UserController(createUseCase, searchUseCase, getUseCase, updateUseCase, userDtoMapper);
        ApiExceptionHandler handler = new ApiExceptionHandler(new MessageServiceStub());
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(handler).build();
    }

    @Test
    void createUser_ReturnsCreated() throws Exception {
        createUseCase.response = user("new@test.com");
        CreateUserEntityDto body = new CreateUserEntityDto("N", "new@test.com", "p",
                new com.felipe.belo.mvp.application.user.dto.UserRoleDto(null, null, null, null, UUID.randomUUID(), "USER", EnumSet.of(Permissions.USER_LIST)));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void listUsers_ReturnsOk() throws Exception {
        searchUseCase.page = new PageImpl<>(List.of(user("a@test.com")), PageRequest.of(0, 20), 1);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getUser_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        getUseCase.response = user("one@test.com");
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("one@test.com"));
    }

    @Test
    void updateUser_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        updateUseCase.response = user("upd@test.com");
        UpdateUserEntityDto body = new UpdateUserEntityDto("ValidName", "upd@test.com", null);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/" + id)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("upd@test.com"));
    }

    @Test
    void getUser_WhenNotFound_Returns404() throws Exception {
        getUseCase.throwNotFound = true;
        UUID id = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(I18nConstants.MESSAGE_USER_NOT_FOUND));
    }

    private static User user(String email) {
        Role role = new Role(UUID.randomUUID(), "USER", EnumSet.of(Permissions.USER_LIST));
        return new User(UUID.randomUUID(), "Name", email, "pwd", role);
    }

    static class StubCreate implements CreateUserUseCase {
        User response;
        @Override public User create(com.felipe.belo.mvp.usecase.user.command.CreateUserCommand command) { return response; }
    }
    static class StubSearch implements SearchUsersUseCase {
        Page<User> page;
        @Override public Page<User> search(SearchRequestDto request) { return page; }
    }
    static class StubGet implements GetUserByIdUseCase {
        User response; boolean throwNotFound;
        @Override public User findById(UUID id) {
            if (throwNotFound) throw new BusinessException(HttpStatus.NOT_FOUND, I18nConstants.MESSAGE_USER_NOT_FOUND);
            return response;
        }
    }
    static class StubUpdate implements UpdateUserUseCase {
        User response;
        @Override public User update(UUID id, com.felipe.belo.mvp.usecase.user.command.UpdateUserCommand command) { return response; }
    }

    static class MessageServiceStub extends MessageService {
        MessageServiceStub() { super(null); }
        @Override public String getMessage(String code, Object[] args) { return code; }
        @Override public String getMessage(String code) { return code; }
    }
}
