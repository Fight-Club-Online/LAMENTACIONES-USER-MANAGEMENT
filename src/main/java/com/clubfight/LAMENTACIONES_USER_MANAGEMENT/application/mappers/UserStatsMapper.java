package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers;

import org.springframework.stereotype.Component;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence.UserStatsDocument;

/**
 * Mapper para las estadísticas de los usuarios
 */
@Component
public class UserStatsMapper {

    public UserStats toDomain(UserStatsDocument document) {

        return new UserStats(
                document.getUserId(),
                document.getWins(),
                document.getLosses(),
                document.getDraws(),
                document.getFollowers(),
                document.getTotalFights(),
                document.getPoints(),
                document.getLevel(),
                document.getStreak()
        );
    }

    public UserStatsDocument toDocument(UserStats stats) {

        UserStatsDocument document = new UserStatsDocument();

        document.setUserId(stats.getUserId());
        document.setWins(stats.getWins());
        document.setLosses(stats.getLosses());
        document.setDraws(stats.getDraws());
        document.setFollowers(stats.getFollowers());
        document.setTotalFights(stats.getTotalFights());
        document.setPoints(stats.getPoints());
        document.setLevel(stats.getLevel());
        document.setStreak(stats.getStreak());

        return document;
    }
}