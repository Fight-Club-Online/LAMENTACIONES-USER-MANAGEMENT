package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;

/**
 * Caso de uso para obtener las estadísticas de un usuario.
 */
public interface GetUserStatsUseCase {
    UserStats getStats(String userId);

}