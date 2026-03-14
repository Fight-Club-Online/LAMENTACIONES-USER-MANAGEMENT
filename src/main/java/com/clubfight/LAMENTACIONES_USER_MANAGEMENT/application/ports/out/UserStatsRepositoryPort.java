package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out;

import java.util.Optional;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;


/**
 * Puerto de salida para las estadísticas de los usuarios.
 */
public interface UserStatsRepositoryPort {
    UserStats save(UserStats stats);
    Optional<UserStats> findByUserId(String userId);

}