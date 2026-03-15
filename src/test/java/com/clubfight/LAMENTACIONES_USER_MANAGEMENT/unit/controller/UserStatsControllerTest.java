package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.controller;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller.UserStatsController;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.GetUserStatsUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserStatsControllerTest {

    @InjectMocks
    private UserStatsController controller;

    @Mock
    private GetUserStatsUseCase service;

    @Test
    void shouldGetStats() {

        String userId = "Oscar";
        UserStats s = new UserStats(userId, 1,0,0,0,0,0,1,0);
        when(service.getStats(userId)).thenReturn(s);

        UserStats res = controller.getStats(userId);

        assertSame(s, res);
        verify(service).getStats(userId);
    }
}