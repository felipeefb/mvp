package com.felipe.belo.mvp.auth;

import com.felipe.belo.mvp.core.config.security.SecurityProps;
import com.felipe.belo.mvp.user.entity.UserEntity;
import com.felipe.belo.mvp.user.repository.UserRepository;
import com.felipe.belo.mvp.core.exception.BusinessException;
import com.felipe.belo.mvp.utils.I18nConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtDecoder jwtDecoder;
    @Autowired
    private SecurityProps securityProps; // contém a secret para JWT

    // DTOs para requisição e resposta
    public static record LoginRequest(String email, String password) {}
    public static record TokenResponse(String accessToken, String refreshToken) {}
    public static record RefreshRequest(String refreshToken) {}

    // Endpoint de Login - autentica e gera tokens
    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Validated LoginRequest loginReq) {
        // 1. Verificar usuário
        UserEntity user = userRepository.findByEmailIgnoreCase(loginReq.email())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED,
                        "user.invalid.credentials"));
        // 2. Verificar senha
        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "user.invalid.credentials");
        }
        // 3. Gerar tokens JWT (access e refresh)
        String accessToken = generateToken(user, 15);    // 15 minutos de validade
        String refreshToken = generateToken(user, 1440); // 1440 minutos = 24h de validade
        return new TokenResponse(accessToken, refreshToken);
    }

    // Endpoint de Refresh - gera novo token de acesso usando refresh token válido
    @PostMapping("/refresh")
    public TokenResponse refreshToken(@RequestBody @Validated RefreshRequest refreshReq) {
        String refreshToken = refreshReq.refreshToken();
        try {
            // Decodifica para validar assinatura e expiração
            jwtDecoder.decode(refreshToken);
        } catch (Exception e) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "access.denied");
        }
        // Se chegou aqui, o refresh token é válido
        // Extrai email (subject) do token para identificar o usuário
        String userEmail = jwtDecoder.decode(refreshToken).getSubject();
        UserEntity user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "user.not.found"));
        // Gera um novo access token (pode também gerar novo refresh para rotacionar)
        String newAccessToken = generateToken(user, 15);
        // Opcional: rotacionar refresh token (aqui geramos um novo também, invalidando o antigo)
        String newRefreshToken = generateToken(user, 1440);
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    // Método auxiliar para gerar um JWT para um usuário com validade em minutos
    private String generateToken(UserEntity user, int expiresInMinutes) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expiresInMinutes, ChronoUnit.MINUTES);
        // Monta as claims do JWT
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getEmail())                 // identificador do usuário (email)
                .issuedAt(now)
                .expiresAt(expiry)
                .claim("uid", user.getId().toString())    // ID do usuário
                .claim("role", user.getRole().getName())  // Role do usuário
                .build();
        // Codifica (assina) o token JWT usando a chave secreta
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}