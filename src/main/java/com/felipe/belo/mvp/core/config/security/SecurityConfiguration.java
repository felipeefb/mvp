package com.felipe.belo.mvp.core.config.security;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.user.entity.UserEntity;
import com.felipe.belo.mvp.user.repository.UserRepository;
import com.felipe.belo.mvp.utils.I18nConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.web.SecurityFilterChain;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger/OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        .requestMatchers("api/v1/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

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
        
        // Create an OctetSequenceKey with explicit algorithm
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
     * @param userRepo repository for user lookup
     * @return user details service
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        // Custom user lookup por email
        return username -> {
            UserEntity user = userRepo.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new BusinessException(
                            HttpStatus.NOT_FOUND,
                            I18nConstants.MESSAGE_USER_NOT_FOUND,
                            username
                    ));

            return User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole().getName())
                    .build();
        };
    }
}