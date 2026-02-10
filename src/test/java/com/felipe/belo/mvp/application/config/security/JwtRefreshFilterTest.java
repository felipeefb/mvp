package com.felipe.belo.mvp.application.config.security;

import com.felipe.belo.mvp.core.component.JwtService;
import com.felipe.belo.mvp.core.domain.model.Role;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.core.utils.permissions.Permissions;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtRefreshFilterTest {

    private JwtService jwtService;
    private UserRepository userRepository;
    private JwtAuthenticationConverter converter;
    private OncePerRequestFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userRepository = mock(UserRepository.class);
        converter = new SecurityConfiguration(null).jwtAuthenticationConverter();
        filter = new JwtRefreshFilter(jwtService, userRepository, converter);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void refreshesExpiredAccessToken() throws Exception {
        String expiredAccess = "expired";
        String refreshToken = "refresh-token";
        String newAccess = "new-access";
        String newRefresh = "new-refresh";

        // expired access token triggers JwtValidationException with invalid_token/expired
        JwtValidationException expiredEx = new JwtValidationException("expired",
                List.of(new OAuth2Error("invalid_token", "token expired", null)));
        when(jwtService.decode(expiredAccess)).thenThrow(expiredEx);

        Jwt refreshJwt = jwt("user@test.com", "USER");
        when(jwtService.decode(refreshToken)).thenReturn(refreshJwt);

        User user = new User(UUID.randomUUID(), "User", "user@test.com", "pwd",
                new Role(UUID.randomUUID(), "USER", EnumSet.of(Permissions.USER_READ)));
        when(userRepository.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(user));

        when(jwtService.generateAccessToken(user)).thenReturn(newAccess);
        when(jwtService.generateRefreshToken(user)).thenReturn(newRefresh);
        when(jwtService.decode(newAccess)).thenReturn(jwt("user@test.com", "USER"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + expiredAccess);
        request.addHeader(JwtRefreshFilter.HEADER_REFRESH_TOKEN, refreshToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader(JwtRefreshFilter.HEADER_NEW_ACCESS_TOKEN)).isEqualTo(newAccess);
        assertThat(response.getHeader(JwtRefreshFilter.HEADER_NEW_REFRESH_TOKEN)).isEqualTo(newRefresh);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user@test.com");
    }

    @Test
    void skipsWhenAccessValid() throws Exception {
        when(jwtService.decode("good")).thenReturn(jwt("user@test.com", "USER"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer good");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getHeader(JwtRefreshFilter.HEADER_NEW_ACCESS_TOKEN)).isNull();
    }

    private static Jwt jwt(String subject, String role) {
        return Jwt.withTokenValue("t")
                .subject(subject)
                .header("alg", "HS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claim("role", role)
                .claim("permissions", List.of("USER_READ"))
                .build();
    }
}
