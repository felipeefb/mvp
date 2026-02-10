package com.felipe.belo.mvp.application.auth;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.belo.mvp.core.component.JwtService;
import com.felipe.belo.mvp.core.component.MessageService;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.exception.ApiExceptionHandler;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;

class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private MessageService messageService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new StubPasswordEncoder();
        jwtService = mock(JwtService.class);
        messageService = new MessageServiceStub();

        AuthController controller = new AuthController(userRepository, passwordEncoder, jwtService);
        ApiExceptionHandler handler = new ApiExceptionHandler(messageService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void login_ReturnsTokens() throws Exception {
        User user = buildUser("login@test.com", "enc-pass");
        when(userRepository.findByEmailIgnoreCase("login@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

        AuthController.LoginRequest body = new AuthController.LoginRequest("login@test.com", "pass");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    void login_WhenPasswordInvalid_Returns401() throws Exception {
        User user = buildUser("login@test.com", "enc-pass");
        when(userRepository.findByEmailIgnoreCase("login@test.com")).thenReturn(Optional.of(user));

        AuthController.LoginRequest body = new AuthController.LoginRequest("login@test.com", "wrong");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("user.invalid.credentials"));
    }

    @Test
    void refresh_ReturnsNewTokens() throws Exception {
        User user = buildUser("user@test.com", "enc-pass");
        when(jwtService.decode("refresh")).thenReturn(null); // only validation pass
        when(userRepository.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh");

        AuthController.RefreshRequest body = new AuthController.RefreshRequest("refresh");
        when(jwtService.decode("refresh")).thenAnswer(inv -> JwtTestHelpers.jwtWithSubject("user@test.com"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));
    }

    @Test
    void refresh_WhenInvalidToken_Returns401() throws Exception {
        when(jwtService.decode(anyString())).thenThrow(new BusinessException(HttpStatus.UNAUTHORIZED, "access.denied"));

        AuthController.RefreshRequest body = new AuthController.RefreshRequest("bad");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("access.denied"));
    }

    private static User buildUser(String email, String password) {
        Role role = new Role(UUID.randomUUID(), "USER", EnumSet.of(Permissions.USER_READ));
        return new User(UUID.randomUUID(), "n", email, password, role);
    }

    static class StubPasswordEncoder implements PasswordEncoder {
        @Override public String encode(CharSequence rawPassword) { return "enc-" + rawPassword; }
        @Override public boolean matches(CharSequence rawPassword, String encodedPassword) { return encode(rawPassword).equals(encodedPassword); }
    }

    static class MessageServiceStub extends MessageService {
        MessageServiceStub() { super(null); }
        @Override public String getMessage(String code, Object[] args) { return code; }
        @Override public String getMessage(String code) { return code; }
    }
}