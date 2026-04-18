package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.UpdateUserStatsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listener que se suscribe a eventos de pelea finalizada para actualizar las estadísticas de los usuarios involucrados.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FightResultListener {

    private final UpdateUserStatsUseCase updateUserStatsUseCase;

    @RabbitListener(queues = "fight.finished.stats.queue")
    public void handleFightFinished(FightFinishedEvent event) {
        log.info("[STATS] Evento recibido: fightId={} result={}",
                event.getFightId(), event.getResult());

        boolean draw = "DRAW".equals(event.getResult());

        updateUserStatsUseCase.applyFightResult(
                event.getWinnerUserId(),
                !draw,
                draw,
                event.getWinnerPointsChange()
        );

        updateUserStatsUseCase.applyFightResult(
                event.getLoserUserId(),
                false,
                draw,
                event.getLoserPointsChange()
        );
    }
}