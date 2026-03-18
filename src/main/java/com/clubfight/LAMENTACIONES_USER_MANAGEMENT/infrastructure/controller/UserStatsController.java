package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.infrastructure.controller;

import org.springframework.web.bind.annotation.*;

import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.application.ports.in.GetUserStatsUseCase;
import com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.model.UserStats;

import lombok.RequiredArgsConstructor;

/**
 * Controlador para las estadísticas del usuario.
 */
@RestController
@RequestMapping("/api/user-stats")
@RequiredArgsConstructor
public class UserStatsController {

    private final GetUserStatsUseCase service;

    @GetMapping("/{userId}")
    public UserStats getStats(@PathVariable String userId) {
        return service.getStats(userId);
    }
}