package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.AzureBlobService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events.commands.PatchUserProfileCommand;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.PatchUserProfileUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AvatarController {

    private final AzureBlobService azureBlobService;
    private final PatchUserProfileUseCase patchUserProfileUseCase;

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
        @PathVariable String userId,
        @RequestParam("file") MultipartFile file
    ) throws Exception {
        String avatarUrl = azureBlobService.uploadAvatar(file, userId);
        PatchUserProfileCommand command = new PatchUserProfileCommand();
        command.setAvatarURL(avatarUrl);
        patchUserProfileUseCase.patch(userId, command);
        return ResponseEntity.ok(Map.of("avatarURL", avatarUrl));
    }
}