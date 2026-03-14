package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller;

import org.springframework.web.bind.annotation.*;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service.GoogleAuthService;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.request.GoogleAuthRequest;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;

import lombok.RequiredArgsConstructor;

/**
 * Controlador para la autenticación de google
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/oauth")
public class OAuthController {

    private final GoogleAuthService googleAuthService;

    @PostMapping("/google")
    public AuthResponse loginWithGoogle(@RequestBody GoogleAuthRequest request) {

        return googleAuthService.authenticate(request.getIdToken());

    }
}