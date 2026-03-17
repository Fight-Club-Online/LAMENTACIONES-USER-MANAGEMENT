package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.GoogleAuthServiceImpl;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserEventPublisher;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config.JwtUtil;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests para GoogleAuthServiceImpl (corregidos).
 */
class GoogleAuthServiceImplTest {

    @InjectMocks
    private GoogleAuthServiceImpl service;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.RefreshTokenService refreshTokenService;

    @Mock
    private UserEventPublisher eventPublisher;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAuthenticateExistingUser() throws Exception {

        String tokenString = "id-token";
        String email = "porras@mail.com";
        String name = "porras";

        GoogleIdTokenVerifier verifierMock = mock(GoogleIdTokenVerifier.class);
        GoogleIdToken idTokenMock = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payloadMock = mock(GoogleIdToken.Payload.class);

        try (MockedConstruction<GoogleIdTokenVerifier.Builder> ignored =
                     mockConstruction(GoogleIdTokenVerifier.Builder.class, (mock, context) -> {
                         when(mock.setAudience(any())).thenReturn(mock);
                         when(mock.build()).thenReturn(verifierMock);
                     });
             MockedStatic<GoogleNetHttpTransport> transportStatic =
                     mockStatic(GoogleNetHttpTransport.class)) {

            transportStatic.when(GoogleNetHttpTransport::newTrustedTransport)
                    .thenReturn(mock(NetHttpTransport.class));

            when(verifierMock.verify(tokenString)).thenReturn(idTokenMock);
            when(idTokenMock.getPayload()).thenReturn(payloadMock);
            when(payloadMock.getEmail()).thenReturn(email);
            when(payloadMock.get("name")).thenReturn(name);

            User existing = User.builder()
                    .id("oscar")
                    .email(email)
                    .verified(true)
                    .build();

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(existing));
            when(jwtUtil.generateToken(existing)).thenReturn("access-token");
            RefreshToken refresh = RefreshToken.builder().token("r-token").userId("oscar").build();
            when(refreshTokenService.createRefreshToken("oscar")).thenReturn(refresh);

            AuthResponse res = service.authenticate(tokenString);

            assertNotNull(res);
            assertEquals("access-token", res.getAccessToken());
            assertEquals("r-token", res.getRefreshToken());
            assertEquals("oscar", res.getUserId());
            verify(userRepositoryPort, never()).save(any());
        }
    }

    @Test
    void shouldCreateUserWhenNotFound() throws Exception {

        String tokenString = "id-token-2";
        String email = "suarez@mail.com";
        String name = "suarez";

        GoogleIdTokenVerifier verifierMock = mock(GoogleIdTokenVerifier.class);
        GoogleIdToken idTokenMock = mock(GoogleIdToken.class);
        GoogleIdToken.Payload payloadMock = mock(GoogleIdToken.Payload.class);

        try (MockedConstruction<GoogleIdTokenVerifier.Builder> ignored =
                     mockConstruction(GoogleIdTokenVerifier.Builder.class, (mock, context) -> {
                         when(mock.setAudience(any())).thenReturn(mock);
                         when(mock.build()).thenReturn(verifierMock);
                     });
             MockedStatic<GoogleNetHttpTransport> transportStatic =
                     mockStatic(GoogleNetHttpTransport.class)) {

            transportStatic.when(GoogleNetHttpTransport::newTrustedTransport)
                    .thenReturn(mock(NetHttpTransport.class));

            when(verifierMock.verify(tokenString)).thenReturn(idTokenMock);
            when(idTokenMock.getPayload()).thenReturn(payloadMock);
            when(payloadMock.getEmail()).thenReturn(email);
            when(payloadMock.get("name")).thenReturn(name);

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

            User saved = User.builder()
                    .id("suarez")
                    .email(email)
                    .verified(true)
                    .build();

            when(userRepositoryPort.save(any(User.class))).thenReturn(saved);
            when(jwtUtil.generateToken(saved)).thenReturn("access-new");
            RefreshToken refresh = RefreshToken.builder().token("suarez-new").userId("suarez").build();
            when(refreshTokenService.createRefreshToken("suarez")).thenReturn(refresh);

            AuthResponse res = service.authenticate(tokenString);

            assertNotNull(res);
            assertEquals("access-new", res.getAccessToken());
            assertEquals("suarez-new", res.getRefreshToken());
            assertEquals("suarez", res.getUserId());

            verify(userRepositoryPort, times(1)).save(any(User.class));
        }
    }
    
    @Test
    void shouldThrowWhenTokenInvalid() throws Exception {
        String tokenString = "bad-token";
        GoogleIdTokenVerifier verifierMock = mock(GoogleIdTokenVerifier.class);
        
        try (MockedConstruction<GoogleIdTokenVerifier.Builder> ignored =
                mockConstruction(GoogleIdTokenVerifier.Builder.class, (mock, context) -> {
                        when(mock.setAudience(any())).thenReturn(mock);
                        when(mock.build()).thenReturn(verifierMock);
                });
                MockedStatic<GoogleNetHttpTransport> transportStatic =
                mockStatic(GoogleNetHttpTransport.class)) {
                        transportStatic.when(GoogleNetHttpTransport::newTrustedTransport)
                        .thenReturn(mock(NetHttpTransport.class));
                        
                        when(verifierMock.verify(tokenString)).thenReturn(null);
                        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.authenticate(tokenString));
                        
                        String msg = ex.getMessage();
                        boolean ok = "Token inválido".equals(msg) || msg.contains("Error verificando Google token");
                        assertTrue(ok, () -> "mensaje inesperado: " + msg);
                }
        }

    @Test
    void shouldWrapExceptionWhenVerifierFails() throws Exception {
        String tokenString = "explode-token";

        GoogleIdTokenVerifier verifierMock = mock(GoogleIdTokenVerifier.class);

        try (MockedConstruction<GoogleIdTokenVerifier.Builder> ignored =
                     mockConstruction(GoogleIdTokenVerifier.Builder.class, (mock, context) -> {
                         when(mock.setAudience(any())).thenReturn(mock);
                         when(mock.build()).thenReturn(verifierMock);
                     });
             MockedStatic<GoogleNetHttpTransport> transportStatic =
                     mockStatic(GoogleNetHttpTransport.class)) {

            transportStatic.when(GoogleNetHttpTransport::newTrustedTransport)
                    .thenReturn(mock(NetHttpTransport.class));

            when(verifierMock.verify(tokenString)).thenThrow(new RuntimeException("arsw"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.authenticate(tokenString));
            assertTrue(ex.getMessage().contains("Error verificando Google token"));
            assertNotNull(ex.getCause());
            assertEquals("arsw", ex.getCause().getMessage());
        }
    }
}