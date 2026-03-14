package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.service;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response.AuthResponse;

/**
 * Servicio para la autenticación de google.
 */
public interface GoogleAuthService {
    AuthResponse authenticate(String idToken);

}
