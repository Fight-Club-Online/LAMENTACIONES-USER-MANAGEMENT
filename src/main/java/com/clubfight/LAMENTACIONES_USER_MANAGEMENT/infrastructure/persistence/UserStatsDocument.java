package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence;

 
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Achievement;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Rank;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Clase documento para las estadísticas de un usuario.
 */
@Data
@Document(collection = "user_stats")
public class UserStatsDocument {

    @Id
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