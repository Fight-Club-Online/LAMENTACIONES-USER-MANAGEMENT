package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.SaveUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.UpdateUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers.UserProfileMapper;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.DeleteUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.GetUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.PatchUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.SaveUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.UpdateUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserProfileRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para el perfil del usuario instrumentado con métricas de negocio nativas.
 */
@Service
@Slf4j 
public class UserProfileService implements SaveUserProfileUseCase, GetUserProfileUseCase, UpdateUserProfileUseCase, PatchUserProfileUseCase, DeleteUserProfileUseCase {

    private final UserProfileRepositoryPort repository;
    private final UserProfileMapper mapper;
    private final MeterRegistry meterRegistry;

    public UserProfileService(UserProfileRepositoryPort repository, UserProfileMapper mapper, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.mapper = mapper;
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Transactional
    public void saveUserProfile(SaveUserProfileCommand command) {
        if (repository.findByUserId(command.getUserId()).isPresent()) {
            return;
        }

        UserProfile profile = mapper.fromSaveCommand(command);
        repository.save(profile);

        trackPerfilOperacion("creacion");
    }
    
    @Override
    public UserProfile getUserProfile(String userId) {
        return repository.findByUserId(userId)
                .map(profile -> {
                    trackPerfilBusqueda("exito");
                    return profile;
                })
                .orElseGet(() -> {
                    trackPerfilBusqueda("no_encontrado");
                    return null;
                });
    }

    @Override
    @Transactional
    public void update(UpdateUserProfileCommand command) {
        UserProfile updatedProfile = mapper.fromUpdateCommand(command);
        repository.save(updatedProfile);

        trackPerfilOperacion("actualizacion_completa");
    }

    @Override
    @Transactional
    public void patch(String userId, PatchUserProfileCommand command) {
        UserProfile profile = repository.findByUserId(userId)
                .orElseThrow(() -> {
                    return new RuntimeException("Perfil no encontrado para parchear");
                });

        if (command.getUsername() != null) profile.setUsername(command.getUsername());
        if (command.getBio() != null) profile.setBio(command.getBio());
        if (command.getCountry() != null) profile.setCountry(command.getCountry());
        if (command.getAvatarURL() != null) profile.setAvatarURL(command.getAvatarURL());
        if (command.getCity() != null) profile.setCity(command.getCity());
        if (command.getNotification() != null) profile.setNotification(command.getNotification());

        repository.save(profile);

        trackPerfilOperacion("parche");
    }

    @Override
    @Transactional
    public void delete(String userId) {
        repository.deleteByUserId(userId);
        trackPerfilOperacion("eliminacion");
    }

    private void trackPerfilOperacion(String tipoOperacion) {
        Counter.builder("perfiles_operaciones_total")
                .description("Total de operaciones mutables ejecutadas sobre los perfiles de usuario")
                .tag("tipo", tipoOperacion)
                .register(meterRegistry)
                .increment();
    }

    private void trackPerfilBusqueda(String resultado) {
        Counter.builder("perfiles_busquedas_total")
                .description("Total de consultas de lectura sobre perfiles de usuario")
                .tag("resultado", resultado)
                .register(meterRegistry)
                .increment();
    }
}