package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Achievement;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Rank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

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
    private Rank rank;
    private Set<Achievement> achievements = new HashSet<>();
}
