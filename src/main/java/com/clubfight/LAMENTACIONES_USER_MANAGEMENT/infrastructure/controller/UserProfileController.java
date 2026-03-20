package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.SaveUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.UpdateUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.DeleteUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.GetUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.PatchUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.SaveUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.UpdateUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

/**
 * Controlador para el perfil del usuario.
 */
@RestController
@RequestMapping("/user-profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final SaveUserProfileUseCase saveUserProfileUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final PatchUserProfileUseCase patchUserProfileUseCase;
    private final DeleteUserProfileUseCase deleteUserProfileUseCase;

    @PostMapping
    public void createProfile(@RequestBody SaveUserProfileCommand command) {
        saveUserProfileUseCase.saveUserProfile(command);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable String userId) {
        UserProfile profile = getUserProfileUseCase.getUserProfile(userId);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{userId}")
    public void updateProfile(@PathVariable String userId, @RequestBody UpdateUserProfileCommand command) {
        command.setUserId(userId);
        updateUserProfileUseCase.update(command);
    }

    @PatchMapping("/{userId}")
    public void patchProfile(@PathVariable String userId, @RequestBody PatchUserProfileCommand command) {
        patchUserProfileUseCase.patch(userId, command);
    }

    @DeleteMapping("/{userId}")
    public void deleteProfile(@PathVariable String userId) {
        deleteUserProfileUseCase.delete(userId);
    }
}