package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.GuestCleanupService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.DeleteUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.RefreshTokenRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GuestCleanupServiceTest {

    @InjectMocks
    private GuestCleanupService service;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Mock
private DeleteUserProfileUseCase deleteUserProfileUseCase; 

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldDeleteExpiredGuests() {

        User guest1 = User.builder().id("g1").email("g1@gmail.com").build();
        User guest2 = User.builder().id("g2").email("g2@gmail.com").build();

        when(userRepositoryPort.findExpiredGuests(any(Instant.class)))
                .thenReturn(List.of(guest1, guest2));

        service.cleanupExpiredGuests();

        verify(refreshTokenRepositoryPort).deleteByUserId("g1");
        verify(refreshTokenRepositoryPort).deleteByUserId("g2");

        verify(userRepositoryPort).delete(guest1);
        verify(userRepositoryPort).delete(guest2);
    }

    @Test
    void shouldDoNothingWhenNoExpiredGuests() {

        when(userRepositoryPort.findExpiredGuests(any(Instant.class)))
                .thenReturn(List.of());

        service.cleanupExpiredGuests();

        verify(refreshTokenRepositoryPort, never()).deleteByUserId(any());
        verify(userRepositoryPort, never()).delete(any());
    }
}