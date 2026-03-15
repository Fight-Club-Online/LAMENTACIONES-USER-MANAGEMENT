package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.RefreshTokenService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.RefreshTokenRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService service;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateRefreshToken() {

  
        String userId = "robin";

        when(refreshTokenRepositoryPort.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = service.createRefreshToken(userId);

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertEquals(userId, token.getUserId());
        assertTrue(token.getExpiration().isAfter(Instant.now()));

        verify(refreshTokenRepositoryPort, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void shouldReturnUserWhenTokenValid() {

        String tokenString = "token";
        String userId = "oscar";

        RefreshToken token = RefreshToken.builder()
                .token(tokenString)
                .userId(userId)
                .expiration(Instant.now().plusSeconds(1000))
                .build();

        User user = User.builder()
                .id(userId)
                .email("oscar@gmail.com")
                .build();

        when(refreshTokenRepositoryPort.findById(tokenString))
                .thenReturn(Optional.of(token));

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.of(user));

        User result = service.verify(tokenString);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void shouldThrowWhenTokenInvalid() {

        String tokenString = "invalid";

        when(refreshTokenRepositoryPort.findById(tokenString))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.verify(tokenString));

        assertEquals("Refresh token inválido", ex.getMessage());
    }

    @Test
    void shouldThrowWhenTokenExpired() {

        String tokenString = "expired";
        String userId = "juan-felipe";

        RefreshToken token = RefreshToken.builder()
                .token(tokenString)
                .userId(userId)
                .expiration(Instant.now().minusSeconds(10))
                .build();

        when(refreshTokenRepositoryPort.findById(tokenString))
                .thenReturn(Optional.of(token));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.verify(tokenString));

        assertEquals("Refresh token expirado", ex.getMessage());

        verify(refreshTokenRepositoryPort).delete(token);
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        String tokenString = "token";
        String userId = "juan-pablo";

        RefreshToken token = RefreshToken.builder()
                .token(tokenString)
                .userId(userId)
                .expiration(Instant.now().plusSeconds(1000))
                .build();

        when(refreshTokenRepositoryPort.findById(tokenString))
                .thenReturn(Optional.of(token));

        when(userRepositoryPort.findById(userId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.verify(tokenString));

        assertEquals("Usuario no encontrado", ex.getMessage());
    }
}