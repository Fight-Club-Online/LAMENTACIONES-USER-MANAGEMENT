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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
    }

    @Test
    void shouldRegisterGuest() {
        RegisterGuestCommand cmd = RegisterGuestCommand.builder()
                .username("guest1")
                .build();

        User guestSaved = User.builder()
                .id("g1")
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
    }
}