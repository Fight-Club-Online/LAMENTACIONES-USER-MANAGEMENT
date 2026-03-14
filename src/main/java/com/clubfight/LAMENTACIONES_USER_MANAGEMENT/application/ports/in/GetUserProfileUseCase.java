package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;

/**
 * Caso de uso para obtener el perfil de un usuario
 */
public interface GetUserProfileUseCase {
    UserProfile getUserProfile(String userId);

}