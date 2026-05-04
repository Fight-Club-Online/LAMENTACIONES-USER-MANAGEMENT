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
        String userId = (String) event.get("userId");
        String reason = (String) event.get("reason");
        log.info("[BAN] Usuario baneado: {}", userId);
        userRepositoryPort.findById(userId).ifPresent(user -> {
            user.setBanned(true);
            user.setBanReason(reason);
            userRepositoryPort.save(user);
        });
    }

    @RabbitListener(queues = "auth.user.suspended.queue")
    public void handleUserSuspended(Map<String, Object> event) {
        String userId = (String) event.get("userId");
        String reason = (String) event.get("reason");
        String expiresAtStr = (String) event.get("expiresAt");
        log.info("[SUSPEND] Usuario suspendido: {}", userId); 
        userRepositoryPort.findById(userId).ifPresent(user -> {
            user.setBanned(true);
            user.setBanReason(reason);
            if (expiresAtStr != null) {
                user.setBanExpiresAt(Instant.parse(expiresAtStr));
            }
            userRepositoryPort.save(user);
        });
    }

    @RabbitListener(queues = "auth.ban.lifted.queue")
    public void handleBanLifted(Map<String, Object> event) {
        String userId = (String) event.get("userId");
        log.info("[LIFT] Ban levantado: {}", userId);
        userRepositoryPort.findById(userId).ifPresent(user -> {
            user.setBanned(false);
            user.setBanReason(null);
            user.setBanExpiresAt(null); 
            userRepositoryPort.save(user);
        });
    }
}