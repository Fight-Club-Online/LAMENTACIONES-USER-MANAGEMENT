package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.controller;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller.OAuthController;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.GoogleAuthService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.GoogleAuthRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class OAuthControllerTest {

    @InjectMocks
    private OAuthController controller;

    @Mock
    private GoogleAuthService googleAuthService;

    @Test
    void shouldReturnAuthResponse() {
        GoogleAuthRequest req = new GoogleAuthRequest();
        req.setIdToken("token-123");

        AuthResponse expected = AuthResponse.builder()
                .accessToken("access-1")
                .refreshToken("refresh-1")
                .userId("robin")
                .email("robin@gmail.com")
                .build();

        when(googleAuthService.authenticate("token-123")).thenReturn(expected);

        AuthResponse actual = controller.loginWithGoogle(req);

        assertNotNull(actual);
        assertSame(expected, actual, "controller should return exactly what the service returns");
        verify(googleAuthService, times(1)).authenticate("token-123");
    }

    @Test
    void shouldPropagateExceptionWhenServiceFails() {

        GoogleAuthRequest req = new GoogleAuthRequest();
        req.setIdToken("bad-token");

        when(googleAuthService.authenticate("bad-token")).thenThrow(new RuntimeException("google error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.loginWithGoogle(req));
        assertEquals("google error", ex.getMessage());
        verify(googleAuthService).authenticate("bad-token");
    }
}