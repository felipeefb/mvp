package com.felipe.belo.mvp.application.config.security;

import com.felipe.belo.mvp.core.component.JwtService;
import com.felipe.belo.mvp.core.domain.model.User;
import com.felipe.belo.mvp.usecase.user.port.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts requests with expired access tokens and attempts to refresh them
 * if a valid refresh token is provided in the 'X-Refresh-Token' header.
 * If the refresh is successful, it authenticates the user for the current request
 * and adds new tokens to the response headers.
 */
public class JwtRefreshFilter extends OncePerRequestFilter {

    static final String HEADER_REFRESH_TOKEN = "X-Refresh-Token";
    static final String HEADER_NEW_ACCESS_TOKEN = "X-New-Access-Token";
    static final String HEADER_NEW_REFRESH_TOKEN = "X-New-Refresh-Token";

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    /**
     * Creates a new JwtRefreshFilter.
     *
     * @param jwtService                 the JWT service
     * @param userRepository             the user repository
     * @param jwtAuthenticationConverter the converter to turn JWT into Authentication
     */
    public JwtRefreshFilter(JwtService jwtService, UserRepository userRepository, JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            try {
                jwtService.decode(accessToken);
            } catch (JwtValidationException e) {
                if (isExpired(e)) {
                    String refreshToken = request.getHeader(HEADER_REFRESH_TOKEN);
                    if (refreshToken != null) {
                        try {
                            Jwt decodedRefreshToken = jwtService.decode(refreshToken);
                            String email = decodedRefreshToken.getSubject();
                            User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
                            if (user != null) {
                                // Generate new tokens
                                String newAccessToken = jwtService.generateAccessToken(user);
                                String newRefreshToken = jwtService.generateRefreshToken(user);

                                // Authenticate for the current request
                                Jwt newJwt = jwtService.decode(newAccessToken);
                                var auth = jwtAuthenticationConverter.convert(newJwt);
                                SecurityContextHolder.getContext().setAuthentication(auth);

                                // Add new tokens to response headers
                                response.addHeader(HEADER_NEW_ACCESS_TOKEN, newAccessToken);
                                response.addHeader(HEADER_NEW_REFRESH_TOKEN, newRefreshToken);
                            }
                        } catch (Exception ex) {
                            // Refresh token invalid or expired, continue and let standard security handle it
                        }
                    }
                }
            } catch (Exception e) {
                // Other JWT errors, continue
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isExpired(JwtValidationException e) {
        return e.getErrors().stream()
                .anyMatch(error -> "invalid_token".equals(error.getErrorCode())
                        && error.getDescription() != null
                        && error.getDescription().contains("expired"));
    }
}
