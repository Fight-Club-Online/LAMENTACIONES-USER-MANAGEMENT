package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.controller;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller.UserProfileController;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.*;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.SaveUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.UpdateUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserProfileControllerTest {

    @InjectMocks
    private UserProfileController controller;

    @Mock
    private SaveUserProfileUseCase saveUserProfileUseCase;

    @Mock
    private GetUserProfileUseCase getUserProfileUseCase;

    @Mock
    private UpdateUserProfileUseCase updateUserProfileUseCase;

    @Mock
    private PatchUserProfileUseCase patchUserProfileUseCase;

    @Mock
    private DeleteUserProfileUseCase deleteUserProfileUseCase;

    @Test
    void shouldCreateProfile() {

        SaveUserProfileCommand cmd = mock(SaveUserProfileCommand.class);

        controller.createProfile(cmd);

        verify(saveUserProfileUseCase, times(1)).saveUserProfile(cmd);
    }
    
    @Test
    void shouldGetProfile() {
        
        String userId = "Juan";
        UserProfile profile = new UserProfile(userId, "juan", "Tunja", "Colombia", "ayuda", "Bogota", true);
        
        when(getUserProfileUseCase.getUserProfile(userId)).thenReturn(profile);
        ResponseEntity<UserProfile> res = controller.getProfile(userId);
        
        assertEquals(200, res.getStatusCode().value());
        assertSame(profile, res.getBody());
        verify(getUserProfileUseCase).getUserProfile(userId);
    }
    
    @Test
    void shouldReturn404WhenProfileNotFound() {

        String userId = "NoExiste";
        
        when(getUserProfileUseCase.getUserProfile(userId)).thenReturn(null);
        ResponseEntity<UserProfile> res = controller.getProfile(userId);
        
        assertEquals(404, res.getStatusCode().value());
        assertNull(res.getBody());
        verify(getUserProfileUseCase).getUserProfile(userId);
    }

    @Test
    void shouldUpdateProfile() {
        String userId = "Rangel";
        UpdateUserProfileCommand cmd = mock(UpdateUserProfileCommand.class);

        controller.updateProfile(userId, cmd);

        verify(cmd).setUserId(userId);
        verify(updateUserProfileUseCase).update(cmd);
    }

    @Test
    void shouldPatchProfile() {

        String userId = "Suarez";
        PatchUserProfileCommand cmd = mock(PatchUserProfileCommand.class);

        controller.patchProfile(userId, cmd);

        verify(patchUserProfileUseCase).patch(userId, cmd);
    }

    @Test
    void shouldDeleteProfile() {

        String userId = "Robin";

        controller.deleteProfile(userId);

        verify(deleteUserProfileUseCase).delete(userId);
    }
}