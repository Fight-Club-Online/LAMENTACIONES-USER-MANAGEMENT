package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.mappers;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums.Rank;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.persistence.UserStatsDocument;
import org.springframework.stereotype.Component;

import java.util.HashSet;

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
                document.getStreak(),
                document.getRank() != null
                        ? document.getRank()
                        : Rank.fromPoints(document.getPoints()),
                document.getAchievements() != null
                        ? document.getAchievements()
                        : new HashSet<>()
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
        document.setRank(stats.getRank());
        document.setAchievements(
                stats.getAchievements() != null
                        ? stats.getAchievements()
                        : new HashSet<>()
        );
        return document;
    }
}