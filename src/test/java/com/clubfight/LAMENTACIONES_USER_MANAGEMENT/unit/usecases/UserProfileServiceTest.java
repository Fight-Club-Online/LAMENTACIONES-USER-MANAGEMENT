package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.usecases;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserProfileRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.UserProfileService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.SaveUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.UpdateUserProfileCommand;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.config.RedisConfig;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para UserProfileService con soporte de instrumentación de métricas de perfiles reales.
 */
@ExtendWith(MockitoExtension.class)
class UserProfileServicePatchIfsTest {

    private UserProfileService service;

    @Mock
    private UserProfileRepositoryPort repository;

    @Mock
    private PatchUserProfileCommand command;

    @Mock
    private com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers.UserProfileMapper mapper;

    private MeterRegistry meterRegistry; 

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        service = new UserProfileService(repository, mapper, meterRegistry);
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
    void shouldPatchUsernameOnly() {
        String userId = "Porras Oscar";
        UserProfile existing = baseProfile(userId);
        
        when(repository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(command.getUsername()).thenReturn("Oscar Porras");
        when(command.getBio()).thenReturn(null);
        when(command.getCountry()).thenReturn(null);
        when(command.getAvatarURL()).thenReturn(null);
        when(command.getCity()).thenReturn(null);
        when(command.getNotification()).thenReturn(null);
        
        service.patch(userId, command);
        
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repository).save(captor.capture());
        
        UserProfile saved = captor.getValue();
        
        assertEquals("Oscar Porras", saved.getUsername());
        assertEquals("Tunja", saved.getBio());
        assertEquals("Colombia", saved.getCountry());
        assertEquals("JuanAvatar", saved.getAvatarURL());
        assertEquals("Bogota", saved.getCity());
        assertTrue(saved.isNotification());

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "parche").count());
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

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "parche").count());
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

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "parche").count());
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

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "parche").count());
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

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "parche").count());
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

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "parche").count());
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

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "parche").count());
    }

    @Test
    void shouldCreateRedisConnectionFactory() {
        RedisConfig config = new RedisConfig();

        ReflectionTestUtils.setField(config, "redisHost", "localhost");
        ReflectionTestUtils.setField(config, "redisPort", 6379);

        LettuceConnectionFactory factory = config.redisConnectionFactory();

        assertNotNull(factory);
    }

    @Test
    void shouldSaveUserProfile() {
        SaveUserProfileCommand cmd = mock(SaveUserProfileCommand.class);
        UserProfile profile = baseProfile("u-save");
        
        when(cmd.getUserId()).thenReturn("u-save");
        when(repository.findByUserId("u-save")).thenReturn(Optional.empty());
        when(mapper.fromSaveCommand(cmd)).thenReturn(profile);
        
        service.saveUserProfile(cmd);
        
        verify(mapper).fromSaveCommand(cmd);
        verify(repository).save(profile);

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "creacion").count());
    }

    @Test
    void shouldUpdateUserProfile() {
        UpdateUserProfileCommand cmd = mock(UpdateUserProfileCommand.class);
        UserProfile profile = baseProfile("u-update");
        
        when(mapper.fromUpdateCommand(cmd)).thenReturn(profile);
        
        service.update(cmd);
        
        verify(mapper).fromUpdateCommand(cmd);
        verify(repository).save(profile);

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "actualizacion_completa").count());
    }

    @Test
    void shouldDeleteUserProfile() {
        String userId = "u-delete";
        
        service.delete(userId);
        
        verify(repository).deleteByUserId(userId);

        assertEquals(1.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "eliminacion").count());
    }

    @Test
    void shouldNotSaveProfileIfAlreadyExists() {
        SaveUserProfileCommand cmd = mock(SaveUserProfileCommand.class);
        
        when(cmd.getUserId()).thenReturn("u-exists");
        when(repository.findByUserId("u-exists")).thenReturn(Optional.of(baseProfile("u-exists")));
        service.saveUserProfile(cmd);
        
        verify(repository, never()).save(any());
        verify(mapper, never()).fromSaveCommand(any());

        assertEquals(0.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "creacion").count());
    }
    
    @Test
    void shouldReturnProfileWithMetricOnSuccess() {
        UserProfile profile = baseProfile("u-find");
        when(repository.findByUserId("u-find")).thenReturn(Optional.of(profile));
        
        UserProfile result = service.getUserProfile("u-find");

        assertNotNull(result);
        assertEquals(1.0, meterRegistry.counter("perfiles_busquedas_total", "resultado", "exito").count());
    }

    @Test
    void shouldReturnNullWhenProfileNotFound() {
        when(repository.findByUserId("u-missing")).thenReturn(Optional.empty());
        
        UserProfile result = service.getUserProfile("u-missing");

        assertNull(result);
        assertEquals(1.0, meterRegistry.counter("perfiles_busquedas_total", "resultado", "no_encontrado").count());
    }

    @Test
    void shouldThrowWhenPatchProfileNotFound() {
        String userId = "u-not-exist";

        when(repository.findByUserId(userId)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> service.patch(userId, command));
        
        verify(repository, never()).save(any());
        assertEquals(0.0, meterRegistry.counter("perfiles_operaciones_total", "tipo", "parche").count());
    }
}