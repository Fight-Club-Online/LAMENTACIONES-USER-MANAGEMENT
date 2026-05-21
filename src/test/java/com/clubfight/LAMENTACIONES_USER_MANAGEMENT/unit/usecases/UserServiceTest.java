package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserEventPublisher;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.LoginUserCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.RegisterGuestCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.RegisterUserCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.RefreshTokenService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.UserService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config.JwtUtil;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para UserService con soporte de instrumentación de métricas de acceso reales en memoria.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService service;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEventPublisher eventPublisher;

    @Mock
    private RefreshTokenService refreshTokenService;

    private MeterRegistry meterRegistry; 

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        service = new UserService(
                userRepositoryPort,
                jwtUtil,
                passwordEncoder,
                eventPublisher,
                refreshTokenService,
                meterRegistry
        );
    }

    @Test
    void shouldRegisterUser() {
        RegisterUserCommand cmd = RegisterUserCommand.builder()
                .email("juan-felipe@gmail.com")
                .password("nose")
                .build();

        when(userRepositoryPort.existsByEmail("juan-felipe@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("nose")).thenReturn("encoded-pass");

        User saved = User.builder()
                .id("juan")
                .email("juan-felipe@gmail.com")
                .password("encoded-pass")
                .role(Role.USER)
                .createdAt(Instant.now())
                .build();

        when(userRepositoryPort.save(any(User.class))).thenReturn(saved);
        when(jwtUtil.generateToken(saved)).thenReturn("acc-token");
        RefreshToken refresh = RefreshToken.builder().token("r-tok").userId("juan").expiration(Instant.now().plusSeconds(10)).build();
        when(refreshTokenService.createRefreshToken("juan")).thenReturn(refresh);

        AuthResponse res = service.register(cmd);

        assertNotNull(res);
        assertEquals("juan", res.getUserId());
        assertEquals("acc-token", res.getAccessToken());
        assertEquals("r-tok", res.getRefreshToken());
        verify(userRepositoryPort).save(argThat(u -> "juan-felipe@gmail.com".equals(u.getEmail()) && u.getPassword() != null));
        verify(eventPublisher, times(1)).publishUserRegistered(any());

        assertEquals(1.0, meterRegistry.counter("usuarios_registro_total", "tipo", "usuario").count());
    }

    @Test
    void shouldThrowWhenRegisterEmailExists() {
        RegisterUserCommand cmd = RegisterUserCommand.builder()
                .email("juan-felipe@gmail.com")
                .password("x")
                .build();

        when(userRepositoryPort.existsByEmail("juan-felipe@gmail.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.register(cmd));
        assertEquals("Usuario ya existe con ese email", ex.getMessage());
        verify(userRepositoryPort, never()).save(any());

        assertEquals(0.0, meterRegistry.counter("usuarios_registro_total", "tipo", "usuario").count());
    }

    @Test
    void shouldLoginUser() {
        LoginUserCommand cmd = LoginUserCommand.builder()
                .email("robinson@gmail.com")
                .password("chispa")
                .build();

        User user = User.builder()
                .id("Robin")
                .email("robinson@gmail.com")
                .password("encoded")
                .role(Role.USER)
                .createdAt(Instant.now())
                .build();

        when(userRepositoryPort.findByEmail("robinson@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("chispa", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("acc");
        RefreshToken refresh = RefreshToken.builder().token("r1").userId("Robin").expiration(Instant.now().plusSeconds(1000)).build();
        when(refreshTokenService.createRefreshToken("Robin")).thenReturn(refresh);

        AuthResponse res = service.login(cmd);

        assertNotNull(res);
        assertEquals("Robin", res.getUserId());
        assertEquals("acc", res.getAccessToken());
        assertEquals("r1", res.getRefreshToken());
        verify(userRepositoryPort).save(any(User.class)); 
        verify(eventPublisher, times(1)).publishUserLoggedIn(any());

        assertEquals(1.0, meterRegistry.counter("usuarios_autenticacion_total", "resultado", "success").count());
    }

    @Test
    void shouldThrowWhenLoginUserNotFound() {
        LoginUserCommand cmd = LoginUserCommand.builder()
                .email("no@mail")
                .password("x")
                .build();

        when(userRepositoryPort.findByEmail("no@mail")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.login(cmd));
        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());

        assertEquals(1.0, meterRegistry.counter("usuarios_autenticacion_total", "resultado", "not_found").count());
    }

    @Test
    void shouldThrowWhenLoginWrongPassword() {
        LoginUserCommand cmd = LoginUserCommand.builder()
                .email("robinson@gmail.com")
                .password("wrong")
                .build();

        User user = User.builder()
                .id("Robin")
                .email("robinson@gmail.com")
                .password("encoded")
                .build();

        when(userRepositoryPort.findByEmail("robinson@gmail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.login(cmd));
        assertEquals("Contraseña incorrecta", ex.getMessage());
        verify(userRepositoryPort, never()).save(any());
        verify(eventPublisher, never()).publishUserLoggedIn(any());

        assertEquals(1.0, meterRegistry.counter("usuarios_autenticacion_total", "resultado", "invalid_password").count());
    }

    @Test
    void shouldRegisterGuest() {
        RegisterGuestCommand cmd = RegisterGuestCommand.builder()
                .username("guest1")
                .build();

        when(userRepositoryPort.findGuestByUsername("guest1")).thenReturn(Optional.empty());

        User guestSaved = User.builder()
                .id("g1")
                .username("guest1")
                .role(Role.GUEST)
                .guestExpiration(Instant.now().plusSeconds(3600))
                .createdAt(Instant.now())
                .build();

        when(userRepositoryPort.save(any(User.class))).thenReturn(guestSaved);
        when(jwtUtil.generateToken(guestSaved)).thenReturn("guest-acc");
        RefreshToken refresh = RefreshToken.builder().token("gr").userId("g1").expiration(Instant.now().plusSeconds(1000)).build();
        when(refreshTokenService.createRefreshToken("g1")).thenReturn(refresh);

        AuthResponse res = service.registerGuest(cmd);

        assertNotNull(res);
        assertEquals("g1", res.getUserId());
        assertEquals("guest-acc", res.getAccessToken());
        assertEquals("gr", res.getRefreshToken());
        verify(eventPublisher, times(1)).publishGuestRegistered(any());

        assertEquals(1.0, meterRegistry.counter("usuarios_registro_total", "tipo", "invitado").count());
    }

    @Test
    void shouldNotTrackRegistrationWhenGuestAlreadyExists() {
        RegisterGuestCommand cmd = RegisterGuestCommand.builder()
                .username("existingGuest")
                .build();

        User existingGuest = User.builder()
                .id("g-existing")
                .username("existingGuest")
                .role(Role.GUEST)
                .build();

        when(userRepositoryPort.findGuestByUsername("existingGuest")).thenReturn(Optional.of(existingGuest));
        when(jwtUtil.generateToken(existingGuest)).thenReturn("guest-acc");
        RefreshToken refresh = RefreshToken.builder().token("gr").userId("g-existing").expiration(Instant.now().plusSeconds(1000)).build();
        when(refreshTokenService.createRefreshToken("g-existing")).thenReturn(refresh);

        AuthResponse res = service.registerGuest(cmd);

        assertNotNull(res);
        verify(userRepositoryPort, never()).save(any());
        
        assertEquals(0.0, meterRegistry.counter("usuarios_registro_total", "tipo", "invitado").count());
    }
}