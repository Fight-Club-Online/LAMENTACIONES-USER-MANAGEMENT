package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Evento que se dispara cuando una pelea ha finalizado, con el resultado y los cambios de puntos para ambos jugadores.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FightFinishedEvent {
    private String fightId;
    private String winnerUserId;
    private String loserUserId;
    private String result;          
    private int winnerPointsChange;
    private int loserPointsChange;
}