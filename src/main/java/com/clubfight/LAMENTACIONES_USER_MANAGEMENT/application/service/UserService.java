package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserRegisteredEvent;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserEventPublisher;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.LoginUserCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.RegisterGuestCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.RegisterUserCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers.UserMapper;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.LoginUserUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.RegisterGuestUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.RegisterUserUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config.JwtUtil;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;


import lombok.RequiredArgsConstructor;

/**
 * Servicio para el usuario.
 */
@Service
@RequiredArgsConstructor
public class UserService implements RegisterUserUseCase, LoginUserUseCase, RegisterGuestUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserEventPublisher eventPublisher;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(RegisterUserCommand command) {

        if (userRepositoryPort.existsByEmail(command.getEmail())) {
            throw new RuntimeException("Usuario ya existe con ese email");
        }

        User user = User.builder()
                .email(command.getEmail())
                .username(command.getUsername())
                .password(passwordEncoder.encode(command.getPassword()))
                .role(Role.USER)
                .verified(false)
                .createdAt(Instant.now())
                .build();

        user = userRepositoryPort.save(user);
        
        eventPublisher.publishUserRegistered(
                UserRegisteredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatarURL(command.getAvatarURL()) 
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build()
        );

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    public AuthResponse login(LoginUserCommand command) {

        User user = userRepositoryPort.findByEmail(command.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        user.setLastLogin(Instant.now());
        userRepositoryPort.save(user);

        eventPublisher.publishUserLoggedIn(
                UserMapper.toUserLoggedInEvent(user, accessToken)
        );

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerGuest(RegisterGuestCommand command) {

        User guest = User.builder()
                .role(Role.GUEST)
                .username(command.getUsername())
                .guestExpiration(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .build();

        guest = userRepositoryPort.save(guest);

        eventPublisher.publishGuestRegistered(
                UserMapper.toGuestRegisteredEvent(guest)
        );

        String accessToken = jwtUtil.generateToken(guest);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(guest.getId());

        return AuthResponse.builder()
                .userId(guest.getId())
                .username(guest.getUsername())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

}