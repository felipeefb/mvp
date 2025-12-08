package com.felipe.belo.mvp.core.config.security;
import com.felipe.belo.mvp.core.component.MessageService;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.user.entity.UserEntity;
import com.felipe.belo.mvp.user.repository.UserRepository;
import com.felipe.belo.mvp.utils.I18nConstants;
import com.felipe.belo.mvp.utils.permissions.Permissions;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring Security configuration for JWT-based authentication and stateless APIs.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private final SecurityProps securityProps;

    /**
     * Creates a new security configuration.
     *
     * @param securityProps application security properties (e.g., JWT secret)
     */
    public SecurityConfiguration(SecurityProps securityProps) {
        this.securityProps = securityProps;
    }

    /**
     * Configures the HTTP security filter chain.
     *
     * @param http the HTTP security builder
     * @return the configured filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            JwtEncoder jwtEncoder,
            UserDetailsService userDetailsService,
            MessageService messageService,
            ObjectMapper objectMapper
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(jwtDecoder)))
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) ->
                                res.sendError(HttpServletResponse.SC_FORBIDDEN))
                        .accessDeniedHandler(customAccessDeniedHandler(messageService, objectMapper))
                );
        return http.build();
    }
    //TODO refactor this to a global handle exception
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(MessageService messageService, ObjectMapper objectMapper) {
        return (request, response, accessDeniedException) -> {
            String message = messageService.getMessage(I18nConstants.MESSAGE_USER_FORBIDDEN);

            ErrorResponse errorResponse = new ErrorResponse(
                    Instant.now().toString(),
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.getReasonPhrase(),
                    I18nConstants.MESSAGE_USER_FORBIDDEN,
                    message
            );

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        };
    }

    /**
     * Standard error payload returned by the API.
     */
    public record ErrorResponse(
            String timestamp,
            int status,
            String error,
            String code,
            String message
    ) {}

    /**
     * Provides a Nimbus JWT encoder using an HS256 symmetric key.
     *
     * @return JWT encoder
     */
    @Bean
    public JwtEncoder jwtEncoder() {
        byte[] secretBytes = securityProps.secret().getBytes(StandardCharsets.UTF_8);
        
        System.out.println("DEBUG: Secret length: " + secretBytes.length + " bytes");
        System.out.println("DEBUG: Secret value: " + securityProps.secret());
        
        // Create an OctetSequenceKey with an explicit algorithm
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(secretBytes)
                .algorithm(JWSAlgorithm.HS256)
                .keyID("mvp-jwt-key")
                .build();
        
        System.out.println("DEBUG: JWK created - Algorithm: " + jwk.getAlgorithm());
        System.out.println("DEBUG: JWK Key ID: " + jwk.getKeyID());
        
        // Create JWKSource from the key
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        
        return new NimbusJwtEncoder(jwkSource);
    }

    /**
     * Provides a Nimbus JWT decoder using an HS256 symmetric key.
     *
     * @return JWT decoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = securityProps.secret().getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    /**
     * Password encoder bean (BCrypt).
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Custom user-details service that loads a user by e-mail.
     *
     * @param userRepository repository for user lookup
     * @return user details service
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            UserEntity user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            List<GrantedAuthority> authorities = new ArrayList<>();

            // Autoridade da role (ex.: ROLE_ADMIN)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

            // MOD: se role == SUPER_ADMIN, conceder todas as permissões
            if ("SUPER_ADMIN".equals(user.getRole().getName())) {
                for (Permissions perm : Permissions.values()) {
                    authorities.add(new SimpleGrantedAuthority(perm.name()));
                }
            } else {
                // Adicionar as permissões do role
                user.getRole().getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.name())));
            }

            return new User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
        };
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(JwtDecoder jwtDecoder) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        // Converter que extrai permissões do claim 'permissions' e trata SUPER_ADMIN
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();

            // Autoridade da role (ex.: ROLE_ADMIN)
            String role = jwt.getClaimAsString("role");
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            if ("SUPER_ADMIN".equals(role)) {
                // Se SUPER_ADMIN, concede todas as permissões
                for (Permissions p : Permissions.values()) {
                    authorities.add(new SimpleGrantedAuthority(p.name()));
                }
            } else {
                // Caso contrário, adiciona apenas as permissões presentes no token
                List<String> perms = jwt.getClaimAsStringList("permissions");
                if (perms != null) {
                    perms.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
                }
            }

            return authorities;
        });
        return converter;
    }
}