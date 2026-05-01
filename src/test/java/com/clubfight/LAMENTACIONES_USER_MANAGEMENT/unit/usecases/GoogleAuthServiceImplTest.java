package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.UserEventPublisher;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.GoogleAuthServiceImpl;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.RefreshTokenService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Role;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.RefreshToken;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config.JwtUtil;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

/**
 * Tests para GoogleAuthServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceImplTest {

    @InjectMocks
    private GoogleAuthServiceImpl service;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserEventPublisher eventPublisher;

    @Test
    void shouldAuthenticateExistingUser() throws Exception {
        String tokenString = "id-token";
        String email = "porras@mail.com";
        String name = "porras";
        String pictureUrl = "https://img.com/avatar.png";

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
            when(payloadMock.get("picture")).thenReturn(pictureUrl);

            User existing = User.builder()
                    .id("oscar")
                    .email(email)
                    .username(name)
                    .verified(true)
                    .role(Role.USER)
                    .build();

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.of(existing));
            when(jwtUtil.generateToken(existing)).thenReturn("access-token");

            RefreshToken refresh = RefreshToken.builder()
                    .token("r-token")
                    .userId("oscar")
                    .build();

            when(refreshTokenService.createRefreshToken("oscar")).thenReturn(refresh);

            AuthResponse res = service.authenticate(tokenString);

            assertNotNull(res);
            assertEquals("access-token", res.getAccessToken());
            assertEquals("r-token", res.getRefreshToken());
            assertEquals("oscar", res.getUserId());
            assertEquals(email, res.getEmail());
            assertEquals(name, res.getUsername());
            assertEquals(Role.USER, res.getRole());

            verify(userRepositoryPort, times(1)).findByEmail(email);
            verify(userRepositoryPort, times(1)).save(existing);
            verify(refreshTokenService, times(1)).createRefreshToken("oscar");
            verify(eventPublisher, times(1)).publishUserLoggedIn(any());
            verify(eventPublisher, never()).publishUserRegistered(any());
        }
    }

    @Test
    void shouldCreateUserWhenNotFound() throws Exception {
        String tokenString = "id-token-2";
        String email = "suarez@mail.com";
        String name = "suarez";
        String pictureUrl = "https://img.com/avatar2.png";

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
            when(payloadMock.get("picture")).thenReturn(pictureUrl);

            when(userRepositoryPort.findByEmail(email)).thenReturn(Optional.empty());

            User saved = User.builder()
                    .id("suarez")
                    .email(email)
                    .username(name)
                    .verified(true)
                    .role(Role.USER)
                    .build();

            when(userRepositoryPort.save(any(User.class))).thenReturn(saved);
            when(jwtUtil.generateToken(saved)).thenReturn("access-new");

            RefreshToken refresh = RefreshToken.builder()
                    .token("suarez-new")
                    .userId("suarez")
                    .build();

            when(refreshTokenService.createRefreshToken("suarez")).thenReturn(refresh);

            AuthResponse res = service.authenticate(tokenString);

            assertNotNull(res);
            assertEquals("access-new", res.getAccessToken());
            assertEquals("suarez-new", res.getRefreshToken());
            assertEquals("suarez", res.getUserId());
            assertEquals(email, res.getEmail());
            assertEquals(name, res.getUsername());
            assertEquals(Role.USER, res.getRole());

            verify(userRepositoryPort, times(1)).findByEmail(email);
            verify(userRepositoryPort, times(2)).save(any(User.class));
            verify(refreshTokenService, times(1)).createRefreshToken("suarez");
            verify(eventPublisher, times(1)).publishUserRegistered(any());
            verify(eventPublisher, times(1)).publishUserLoggedIn(any());
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

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.authenticate(tokenString));

            assertEquals("Error en autenticación Google: Token de Google inválido", ex.getMessage());
            verify(userRepositoryPort, never()).findByEmail(any());
            verify(userRepositoryPort, never()).save(any());
            verify(refreshTokenService, never()).createRefreshToken(any());
            verify(eventPublisher, never()).publishUserRegistered(any());
            verify(eventPublisher, never()).publishUserLoggedIn(any());
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

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.authenticate(tokenString));

            assertEquals("Error en autenticación Google: arsw", ex.getMessage());
            assertNotNull(ex.getCause());
            assertEquals("arsw", ex.getCause().getMessage());
        }
    }
}