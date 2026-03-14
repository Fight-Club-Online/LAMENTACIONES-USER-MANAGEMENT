package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase para las estadísticas del usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor  
public class UserStats {
    private String userId;
    private int wins;
    private int losses;
    private int draws;
    private int followers;
    private int totalFights;
    private int points;
    private int level;
    private int streak;

}
