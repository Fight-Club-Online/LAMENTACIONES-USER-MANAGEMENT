package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import org.springframework.stereotype.Service;

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

/**
 * Servicio para el perfil del usuario.
 */
@Service
@RequiredArgsConstructor
public class UserProfileService implements SaveUserProfileUseCase, GetUserProfileUseCase , UpdateUserProfileUseCase, PatchUserProfileUseCase, DeleteUserProfileUseCase {

    private final UserProfileRepositoryPort repository;
    private final UserProfileMapper mapper;

    @Override
    public void saveUserProfile(SaveUserProfileCommand command) {
        UserProfile profile = mapper.fromSaveCommand(command);

        repository.save(profile);
    }

    @Override
    public UserProfile getUserProfile(String userId) {

        return repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    @Override
    public void update(UpdateUserProfileCommand command) {

        UserProfile profile = mapper.fromUpdateCommand(command);
        repository.save(profile);
    }

    @Override
    public void patch(String userId, PatchUserProfileCommand command) {

        UserProfile profile = repository.findByUserId(userId)
                .orElseThrow();

        if (command.getBio() != null) profile.setBio(command.getBio());
        if (command.getCountry() != null) profile.setCountry(command.getCountry());
        if (command.getAvatarURL() != null) profile.setAvatarURL(command.getAvatarURL());
        if (command.getCity() != null) profile.setCity(command.getCity());
        if (command.getNotification() != null) profile.setNotification(command.getNotification());

        repository.save(profile);
    }

    @Override
    public void delete(String userId) {
        repository.deleteByUserId(userId);
    }
}