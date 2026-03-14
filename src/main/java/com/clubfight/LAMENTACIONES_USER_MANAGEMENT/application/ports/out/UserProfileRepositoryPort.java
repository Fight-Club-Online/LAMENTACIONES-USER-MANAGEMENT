package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out;

import java.util.Optional;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserProfile;

/**
 * Puerto de salida para la gestion del perfil de un usuario.
 */
public interface UserProfileRepositoryPort {
    UserProfile save(UserProfile profile);
    Optional<UserProfile> findByUserId(String userId);
    void deleteByUserId(String userId);

}