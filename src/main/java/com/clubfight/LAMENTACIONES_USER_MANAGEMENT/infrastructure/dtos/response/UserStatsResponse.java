package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.dtos.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta para las estadísticas del usuario.
 * 
 */
@Data
@Builder
public class UserStatsResponse {
    private int wins;
    private int losses;
    private int draws;
    private int followers;
    private int totalFights;
    private int points;
    private int level;
    private int streak;
}
