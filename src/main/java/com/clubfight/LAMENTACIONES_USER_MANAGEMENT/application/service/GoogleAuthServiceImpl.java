package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserEventPublisher;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserLoggedInEvent;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserRegisteredEvent;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config.JwtUtil;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de autenticación de google.
 */
@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserEventPublisher eventPublisher;

    @Value("${google.client.id}")
    private String clientId;

    @Override
    @Transactional 
    public AuthResponse authenticate(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new RuntimeException("Token de Google inválido");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");

            AtomicBoolean isNewUser = new AtomicBoolean(false);
            User user = userRepositoryPort.findByEmail(email)
                    .orElseGet(() -> {
                        isNewUser.set(true);
                        User newUser = User.builder()
                                .email(email)
                                .username(name)
                                .verified(true)
                                .role(Role.USER)
                                .createdAt(Instant.now())
                                .build();
                        return userRepositoryPort.save(newUser);
                    });

            if (isNewUser.get()) {
                eventPublisher.publishUserRegistered(UserRegisteredEvent.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .avatarURL(pictureUrl)
                        .role(user.getRole()) 
                        .createdAt(user.getCreatedAt())
                        .build());
            }


            user.setLastLogin(Instant.now());
            userRepositoryPort.save(user);

            String accessToken = jwtUtil.generateToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            eventPublisher.publishUserLoggedIn(UserLoggedInEvent.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .token(accessToken)
                    .loginAt(user.getLastLogin())
                    .build());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .userId(user.getId())
                    .role(user.getRole())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error en autenticación Google: " + e.getMessage(), e);
        }
    }
}