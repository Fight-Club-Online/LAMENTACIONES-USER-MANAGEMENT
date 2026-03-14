package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.RefreshTokenRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para el token de refresco.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    public RefreshToken createRefreshToken(String userId){

        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userId(userId)
                .expiration(Instant.now().plusSeconds(604800)) // 7 días
                .build();

        return refreshTokenRepositoryPort.save(refreshToken);
    }

    public User verify(String token){

        RefreshToken refreshToken = refreshTokenRepositoryPort.findById(token)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        if(refreshToken.getExpiration().isBefore(Instant.now())){
            refreshTokenRepositoryPort.delete(refreshToken);
            throw new RuntimeException("Refresh token expirado");
        }

        return userRepositoryPort.findById(refreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}