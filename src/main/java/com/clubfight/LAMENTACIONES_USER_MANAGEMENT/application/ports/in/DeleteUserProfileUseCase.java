package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

/**
 * Caso de uso de eliminar el perfil de un usuario.
 */
public interface DeleteUserProfileUseCase {
    void delete(String userId);

}
