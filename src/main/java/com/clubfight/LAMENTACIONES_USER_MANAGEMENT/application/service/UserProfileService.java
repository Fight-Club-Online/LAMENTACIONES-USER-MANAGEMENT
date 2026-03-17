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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para el perfil del usuario.
 */
@Service
@RequiredArgsConstructor
@Slf4j 
public class UserProfileService implements SaveUserProfileUseCase, GetUserProfileUseCase, UpdateUserProfileUseCase, PatchUserProfileUseCase, DeleteUserProfileUseCase {

    private final UserProfileRepositoryPort repository;
    private final UserProfileMapper mapper;

    @Override
    @Transactional
    public void saveUserProfile(SaveUserProfileCommand command) {
        
        if (repository.findByUserId(command.getUserId()).isPresent()) {
            log.info("El perfil para el usuario {} ya existe. Omitiendo creación.", command.getUserId());
            return;
        }

        UserProfile profile = mapper.fromSaveCommand(command);
        
        log.info("Guardando perfil nuevo: userId={}, username={}", profile.getUserId(), profile.getUsername());
        repository.save(profile);
    }
    
    @Override
    public UserProfile getUserProfile(String userId) {
        return repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado para el usuario: " + userId));
    }

    @Override
    @Transactional
    public void update(UpdateUserProfileCommand command) {
        UserProfile updatedProfile = mapper.fromUpdateCommand(command);
        repository.save(updatedProfile);
    }

    @Override
    @Transactional
    public void patch(String userId, PatchUserProfileCommand command) {
        UserProfile profile = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado para parchear"));

        if (command.getBio() != null) profile.setBio(command.getBio());
        if (command.getCountry() != null) profile.setCountry(command.getCountry());
        if (command.getAvatarURL() != null) profile.setAvatarURL(command.getAvatarURL());
        if (command.getCity() != null) profile.setCity(command.getCity());
        if (command.getNotification() != null) profile.setNotification(command.getNotification());

        log.info("Aplicando patch al perfil de: {}", userId);
        repository.save(profile);
    }

    @Override
    @Transactional
    public void delete(String userId) {
        repository.deleteByUserId(userId);
        log.info("Perfil eliminado para el usuario: {}", userId);
    }
}