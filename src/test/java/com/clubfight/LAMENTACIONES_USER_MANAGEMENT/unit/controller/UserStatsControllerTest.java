package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.controller;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.GetUserStatsUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller.UserStatsController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStatsControllerTest {

    @InjectMocks
    private UserStatsController controller;

    @Mock
    private GetUserStatsUseCase service;

    @Test
    void shouldGetStats() {
        String userId = "Oscar";

        UserStats s = mock(UserStats.class);
        when(service.getStats(userId)).thenReturn(s);

        UserStats res = controller.getStats(userId);

        assertSame(s, res);
        verify(service).getStats(userId);
    }
}