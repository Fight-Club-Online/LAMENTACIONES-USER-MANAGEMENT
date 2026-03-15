package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserProfileRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.UserProfileService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServicePatchIfsTest {

    @InjectMocks
    private UserProfileService service;

    @Mock
    private UserProfileRepositoryPort repository;

    @Mock
    private PatchUserProfileCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private UserProfile baseProfile(String userId) {
        return new UserProfile(
                userId,
                "JuanPakas",
                "Tunja",
                "Colombia",
                "JuanAvatar",
                "Bogota",
                Boolean.TRUE
        );
    }

    @Test
    void shouldPatchBioOnly() {

        String userId = "u-bio";
        UserProfile existing = baseProfile(userId);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));

        when(command.getBio()).thenReturn("newBio");
        when(command.getCountry()).thenReturn(null);
        when(command.getAvatarURL()).thenReturn(null);
        when(command.getCity()).thenReturn(null);
        when(command.getNotification()).thenReturn(null);

        service.patch(userId, command);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repository).save(captor.capture());

        UserProfile saved = captor.getValue();

        assertEquals("newBio", saved.getBio());
        assertEquals("Colombia", saved.getCountry());
        assertEquals("JuanAvatar", saved.getAvatarURL());
        assertEquals("Bogota", saved.getCity());
        assertTrue(saved.isNotification());
    }

    @Test
    void shouldPatchCountryOnly() {

        String userId = "u-country";
        UserProfile existing = baseProfile(userId);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));

        when(command.getBio()).thenReturn(null);
        when(command.getCountry()).thenReturn("Mexico");
        when(command.getAvatarURL()).thenReturn(null);
        when(command.getCity()).thenReturn(null);
        when(command.getNotification()).thenReturn(null);

        service.patch(userId, command);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repository).save(captor.capture());

        UserProfile saved = captor.getValue();

        assertEquals("Tunja", saved.getBio());
        assertEquals("Mexico", saved.getCountry());
        assertEquals("JuanAvatar", saved.getAvatarURL());
        assertEquals("Bogota", saved.getCity());
        assertTrue(saved.isNotification());
    }

    @Test
    void shouldPatchAvatarOnly() {

        String userId = "u-avatar";
        UserProfile existing = baseProfile(userId);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));

        when(command.getBio()).thenReturn(null);
        when(command.getCountry()).thenReturn(null);
        when(command.getAvatarURL()).thenReturn("newAvatarUrl");
        when(command.getCity()).thenReturn(null);
        when(command.getNotification()).thenReturn(null);

        service.patch(userId, command);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repository).save(captor.capture());

        UserProfile saved = captor.getValue();

        assertEquals("Tunja", saved.getBio());
        assertEquals("Colombia", saved.getCountry());
        assertEquals("newAvatarUrl", saved.getAvatarURL());
        assertEquals("Bogota", saved.getCity());
        assertTrue(saved.isNotification());
    }

    @Test
    void shouldPatchCityOnly() {

        String userId = "u-city";
        UserProfile existing = baseProfile(userId);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));

        when(command.getBio()).thenReturn(null);
        when(command.getCountry()).thenReturn(null);
        when(command.getAvatarURL()).thenReturn(null);
        when(command.getCity()).thenReturn("Medellin");
        when(command.getNotification()).thenReturn(null);

        service.patch(userId, command);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repository).save(captor.capture());

        UserProfile saved = captor.getValue();

        assertEquals("Tunja", saved.getBio());
        assertEquals("Colombia", saved.getCountry());
        assertEquals("JuanAvatar", saved.getAvatarURL());
        assertEquals("Medellin", saved.getCity());
        assertTrue(saved.isNotification());
    }

    @Test
    void shouldPatchNotificationOnly() {

        String userId = "u-notif";
        UserProfile existing = baseProfile(userId);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));

        when(command.getBio()).thenReturn(null);
        when(command.getCountry()).thenReturn(null);
        when(command.getAvatarURL()).thenReturn(null);
        when(command.getCity()).thenReturn(null);
        when(command.getNotification()).thenReturn(Boolean.FALSE);

        service.patch(userId, command);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repository).save(captor.capture());

        UserProfile saved = captor.getValue();

        assertEquals("Tunja", saved.getBio());
        assertEquals("Colombia", saved.getCountry());
        assertEquals("JuanAvatar", saved.getAvatarURL());
        assertEquals("Bogota", saved.getCity());
        assertFalse(saved.isNotification());
    }

    @Test
    void shouldNotChangeAnythingWhenAllNull() {

        String userId = "u-none";
        UserProfile existing = baseProfile(userId);

        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));

        when(command.getBio()).thenReturn(null);
        when(command.getCountry()).thenReturn(null);
        when(command.getAvatarURL()).thenReturn(null);
        when(command.getCity()).thenReturn(null);
        when(command.getNotification()).thenReturn(null);

        service.patch(userId, command);

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repository).save(captor.capture());

        UserProfile saved = captor.getValue();

        assertEquals("Tunja", saved.getBio());
        assertEquals("Colombia", saved.getCountry());
        assertEquals("JuanAvatar", saved.getAvatarURL());
        assertEquals("Bogota", saved.getCity());
        assertTrue(saved.isNotification());
    }
}