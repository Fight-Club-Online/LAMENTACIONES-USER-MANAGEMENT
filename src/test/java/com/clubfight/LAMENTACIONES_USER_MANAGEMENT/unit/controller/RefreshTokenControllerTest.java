package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.controller;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller.RefreshTokenController;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.RefreshTokenService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config.JwtUtil;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.RefreshRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RefreshTokenControllerTest {

    @InjectMocks
    private RefreshTokenController controller;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    void shouldRefreshToken() {
 
        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("r-1");

        User user = User.builder().id("u-1").email("juan-felipe@gmail.com").build();

        when(refreshTokenService.verify("r-1")).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("access-1");

        AuthResponse res = controller.refreshToken(req);

        assertNotNull(res);
        assertEquals("access-1", res.getAccessToken());
        assertEquals("r-1", res.getRefreshToken());
        verify(refreshTokenService, times(1)).verify("r-1");
        verify(jwtUtil, times(1)).generateToken(user);
    }

    @Test
    void shouldPropagateExceptionWhenVerifyFails() {
        RefreshRequest req = new RefreshRequest();
        req.setRefreshToken("bad");

        when(refreshTokenService.verify("bad")).thenThrow(new RuntimeException("invalid"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.refreshToken(req));
        assertEquals("invalid", ex.getMessage());
        verify(refreshTokenService).verify("bad");
    }
}