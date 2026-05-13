package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.unit.controller;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.PatchUserProfileUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.AzureBlobService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller.AvatarController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AvatarControllerTest {

    @InjectMocks
    private AvatarController controller;

    @Mock
    private AzureBlobService azureBlobService;

    @Mock
    private PatchUserProfileUseCase patchUserProfileUseCase;

    @Test
    void shouldUploadAvatarAndPatchProfileSuccessfully() throws Exception {
        String userId = "user-123";
        String expectedUrl = "https://storage.blob.core.windows.net/avatars/user-123/abc.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "fake-image-bytes".getBytes()
        );

        when(azureBlobService.uploadAvatar(file, userId)).thenReturn(expectedUrl);
        doNothing().when(patchUserProfileUseCase).patch(eq(userId), any(PatchUserProfileCommand.class));

        ResponseEntity<Map<String, String>> response = controller.uploadAvatar(userId, file);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(expectedUrl, response.getBody().get("avatarURL"));

        verify(azureBlobService, times(1)).uploadAvatar(file, userId);
        verify(patchUserProfileUseCase, times(1)).patch(eq(userId), any(PatchUserProfileCommand.class));
    }

    @Test
    void shouldSetAvatarURLInPatchCommand() throws Exception {
        String userId = "user-456";
        String expectedUrl = "https://storage.blob.core.windows.net/avatars/user-456/xyz.png";

        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", "png-bytes".getBytes()
        );

        when(azureBlobService.uploadAvatar(file, userId)).thenReturn(expectedUrl);

        ArgumentCaptor<PatchUserProfileCommand> captor = ArgumentCaptor.forClass(PatchUserProfileCommand.class);

        controller.uploadAvatar(userId, file);

        verify(patchUserProfileUseCase).patch(eq(userId), captor.capture());
        assertEquals(expectedUrl, captor.getValue().getAvatarURL());
    }

    @Test
    void shouldPropagateExceptionWhenAzureFails() throws Exception {
        String userId = "user-789";
        MockMultipartFile file = new MockMultipartFile(
                "file", "broken.jpg", "image/jpeg", new byte[0]
        );

        when(azureBlobService.uploadAvatar(file, userId))
                .thenThrow(new RuntimeException("Azure Storage no está configurado"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.uploadAvatar(userId, file));

        assertEquals("Azure Storage no está configurado", ex.getMessage());

        verify(azureBlobService, times(1)).uploadAvatar(file, userId);
        verify(patchUserProfileUseCase, never()).patch(any(), any());
    }

    @Test
    void shouldPropagateExceptionWhenPatchFails() throws Exception {
        String userId = "user-999";
        String avatarUrl = "https://storage.blob.core.windows.net/avatars/user-999/img.jpg";

        MockMultipartFile file = new MockMultipartFile(
                "file", "img.jpg", "image/jpeg", "bytes".getBytes()
        );

        when(azureBlobService.uploadAvatar(file, userId)).thenReturn(avatarUrl);
        doThrow(new RuntimeException("Perfil no encontrado para parchear"))
                .when(patchUserProfileUseCase).patch(eq(userId), any(PatchUserProfileCommand.class));


        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.uploadAvatar(userId, file));
        assertEquals("Perfil no encontrado para parchear", ex.getMessage());
        verify(azureBlobService, times(1)).uploadAvatar(file, userId);
        verify(patchUserProfileUseCase, times(1)).patch(eq(userId), any(PatchUserProfileCommand.class));
    }
}