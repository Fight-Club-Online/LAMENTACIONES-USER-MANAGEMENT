package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

/**
 * Caso de uso para actualizar las estadísticas de un usuario después de una pelea, 
 * aplicando los cambios de puntos y actualizando el nivel y racha según corresponda.
 */
public interface UpdateUserStatsUseCase {
    void applyFightResult(String userId, boolean won, boolean draw, int pointsChange);
}