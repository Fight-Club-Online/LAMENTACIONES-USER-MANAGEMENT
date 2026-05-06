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

    // En BanEventListener.java — cambia Map<String, Object> por el tipo correcto

    @RabbitListener(queues = "auth.user.banned.queue")
    public void handleUserBanned(UserBannedPayload event) { // clase simple abajo
        log.info("[BAN] userId: {}, reason: {}", event.getUserId(), event.getReason());
        userRepositoryPort.findById(event.getUserId()).ifPresent(user -> {
            user.setBanned(true);
            user.setBanReason(event.getReason());
            userRepositoryPort.save(user);
            log.info("[BAN] Usuario {} actualizado a banned=true", event.getUserId());
        });
    }

    @RabbitListener(queues = "auth.user.suspended.queue")
    public void handleUserSuspended(UserBannedPayload event) {
        userRepositoryPort.findById(event.getUserId()).ifPresent(user -> {
            user.setBanned(true);
            user.setBanReason(event.getReason());
            if (event.getExpiresAt() != null) {
                user.setBanExpiresAt(event.getExpiresAt());
            }
            userRepositoryPort.save(user);
            log.info("[SUSPEND] Usuario {} suspendido", event.getUserId());
        });
    }

    @RabbitListener(queues = "auth.ban.lifted.queue")
    public void handleBanLifted(UserBannedPayload event) {
        userRepositoryPort.findById(event.getUserId()).ifPresent(user -> {
            user.setBanned(false);
            user.setBanReason(null);
            user.setBanExpiresAt(null);
            userRepositoryPort.save(user);
            log.info("[LIFT] Usuario {} desbaneado", event.getUserId());
        });
    }
}