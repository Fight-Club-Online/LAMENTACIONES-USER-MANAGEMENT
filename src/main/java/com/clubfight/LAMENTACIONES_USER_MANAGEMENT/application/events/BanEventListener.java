package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.events;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.out.UserRepositoryPort;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BanEventListener {

    private final UserRepositoryPort userRepositoryPort;

    @RabbitListener(queues = "auth.user.banned.queue")
    public void handleUserBanned(Map<String, Object> event) {
        try {
            log.info("[BAN] Evento recibido RAW: {}", event);
            String userId = (String) event.get("userId");
            String reason = event.get("reason") != null ? event.get("reason").toString() : "UNKNOWN";
            log.info("[BAN] Usuario baneado: {} razón: {}", userId, reason);
            userRepositoryPort.findById(userId).ifPresent(user -> {
                user.setBanned(true);
                user.setBanReason(reason);
                userRepositoryPort.save(user);
                log.info("[BAN] Usuario {} actualizado a banned=true", userId);
            });
        } catch (Exception e) {
            log.error("[BAN] Error procesando evento: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "auth.user.suspended.queue")
    public void handleUserSuspended(Map<String, Object> event) {
        try {
            log.info("[SUSPEND] Evento recibido RAW: {}", event);
            String userId = (String) event.get("userId");
            String reason = event.get("reason") != null ? event.get("reason").toString() : "UNKNOWN";
            String expiresAtStr = event.get("expiresAt") != null ? event.get("expiresAt").toString() : null;
            log.info("[SUSPEND] Usuario suspendido: {}", userId);
            userRepositoryPort.findById(userId).ifPresent(user -> {
                user.setBanned(true);
                user.setBanReason(reason);
                if (expiresAtStr != null) {
                    user.setBanExpiresAt(Instant.parse(expiresAtStr));
                }
                userRepositoryPort.save(user);
                log.info("[SUSPEND] Usuario {} actualizado a banned=true", userId);
            });
        } catch (Exception e) {
            log.error("[SUSPEND] Error procesando evento: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "auth.ban.lifted.queue")
    public void handleBanLifted(Map<String, Object> event) {
        try {
            log.info("[LIFT] Evento recibido RAW: {}", event);
            String userId = (String) event.get("userId");
            log.info("[LIFT] Ban levantado: {}", userId);
            userRepositoryPort.findById(userId).ifPresent(user -> {
                user.setBanned(false);
                user.setBanReason(null);
                user.setBanExpiresAt(null);
                userRepositoryPort.save(user);
                log.info("[LIFT] Usuario {} actualizado a banned=false", userId);
            });
        } catch (Exception e) {
            log.error("[LIFT] Error procesando evento: {}", e.getMessage(), e);
        }
    }
}