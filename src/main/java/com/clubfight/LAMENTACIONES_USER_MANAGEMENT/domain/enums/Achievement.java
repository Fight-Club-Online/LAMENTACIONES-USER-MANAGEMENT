package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Achievement {

    PRIMERA_SANGRE(
        "Primera Sangre",
        "Ganar el primer combate",
        "🔥"
    ),
    VETERANO(
        "Veterano",
        "50+ combates disputados",
        "⚔️"
    ),
    CAZADOR(
        "Cazador",
        "Derrotar a 10 oponentes únicos",
        "🎯"
    ),
    LEYENDA(
        "Leyenda",
        "Alcanzar 100 victorias",
        "👑"
    ),

    RACHA_DE_5(
        "Racha de 5",
        "5 victorias consecutivas",
        "⚡"
    ),
    RACHA_DE_10(
        "Racha de 10",
        "10 victorias consecutivas",
        "💥"
    ),
    INVICTO_DEL_DIA(
        "Invicto del Día",
        "Ganar 5 peleas en un día",
        "☀️"
    ),

    MAESTRO_DEL_RING(
        "Maestro del Ring",
        "Alcanzar rango Platino",
        "🏆"
    );

    private final String displayName;
    private final String description;
    private final String icon;
}