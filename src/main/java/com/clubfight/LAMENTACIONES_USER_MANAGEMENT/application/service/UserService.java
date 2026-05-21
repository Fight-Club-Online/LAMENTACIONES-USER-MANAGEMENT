package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserRegisteredEvent;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserEventPublisher;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserLoggedInEvent;
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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class UserService implements RegisterUserUseCase, LoginUserUseCase, RegisterGuestUseCase {

        private final UserRepositoryPort userRepositoryPort;
        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;
        private final UserEventPublisher eventPublisher;
        private final RefreshTokenService refreshTokenService;
        private final MeterRegistry meterRegistry; 

        public UserService(
                        UserRepositoryPort userRepositoryPort,
                        JwtUtil jwtUtil,
                        PasswordEncoder passwordEncoder,
                        UserEventPublisher eventPublisher,
                        RefreshTokenService refreshTokenService,
                        MeterRegistry meterRegistry) {
                this.userRepositoryPort = userRepositoryPort;
                this.jwtUtil = jwtUtil;
                this.passwordEncoder = passwordEncoder;
                this.eventPublisher = eventPublisher;
                this.refreshTokenService = refreshTokenService;
                this.meterRegistry = meterRegistry;
        }

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
                                                .build());

                String accessToken = jwtUtil.generateToken(user);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

                trackUserRegistration("usuario");

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
        public AuthResponse login(LoginUserCommand command) {
                User user = userRepositoryPort.findByEmail(command.getEmail())
                                .orElseThrow(() -> {
                                        trackUserLogin("not_found");
                                        return new RuntimeException("Usuario no encontrado");
                                });

                if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
                        trackUserLogin("invalid_password");
                        throw new RuntimeException("Contraseña incorrecta");
                }

                if (user.isBanned()) {
                        if (user.getBanExpiresAt() != null && Instant.now().isAfter(user.getBanExpiresAt())) {
                                user.setBanned(false);
                                user.setBanReason(null);
                                user.setBanExpiresAt(null);
                                userRepositoryPort.save(user);
                        } else {
                                trackUserLogin("banned");
                                throw new RuntimeException(
                                                "Tu cuenta ha sido sancionada. Razón: " + user.getBanReason());
                        }
                }

                String accessToken = jwtUtil.generateToken(user);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

                user.setLastLogin(Instant.now());
                userRepositoryPort.save(user);

                eventPublisher.publishUserLoggedIn(
                                UserLoggedInEvent.builder()
                                                .userId(user.getId())
                                                .username(user.getUsername())
                                                .token(accessToken)
                                                .loginAt(user.getLastLogin())
                                                .build());
                trackUserLogin("success");

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
                final java.util.concurrent.atomic.AtomicBoolean isNewGuest = new java.util.concurrent.atomic.AtomicBoolean(false);

                User guest = userRepositoryPort.findGuestByUsername(command.getUsername())
                                .filter(u -> u.getRole() == Role.GUEST)
                                .orElseGet(() -> {
                                        isNewGuest.set(true);
                                        User newGuest = User.builder()
                                                        .role(Role.GUEST)
                                                        .username(command.getUsername())
                                                        .guestExpiration(Instant.now().plusSeconds(3600))
                                                        .createdAt(Instant.now())
                                                        .build();
                                        return userRepositoryPort.save(newGuest);
                                });

                eventPublisher.publishGuestRegistered(
                                UserMapper.toGuestRegisteredEvent(guest));

                String accessToken = jwtUtil.generateToken(guest);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(guest.getId());
                if (isNewGuest.get()) {
                        trackUserRegistration("invitado");
                }

                return AuthResponse.builder()
                                .userId(guest.getId())
                                .username(guest.getUsername())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .build();
        }

        private void trackUserRegistration(String tipo) {
                Counter.builder("usuarios_registro_total")
                        .description("Total de registros nuevos creados en el sistema")
                        .tag("tipo", tipo)
                        .register(meterRegistry)
                        .increment();
        }

        private void trackUserLogin(String resultado) {
                Counter.builder("usuarios_autenticacion_total")
                        .description("Total de intentos de inicio de sesión locales")
                        .tag("resultado", resultado)
                        .register(meterRegistry)
                        .increment();
        }
}