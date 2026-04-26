package com.clubfight.LAMENTACIONES_USER_MANAGEMENT.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rank {

    HIERRO_I  (0,    99,   "Hierro I",   "HIERRO"),
    HIERRO_II   (100,  199,  "Hierro II",    "HIERRO"),
    HIERRO_III    (200,  299,  "Hierro III",     "HIERRO"),

    BRONCE_I  (300,  399,  "Bronce I",   "BRONCE"),
    BRONCE_II   (400,  499,  "Bronce II",    "BRONCE"),
    BRONCE_III    (500,  599,  "Bronce III",     "BRONCE"),

    PLATA_I  (600,  699,  "Plata I",    "PLATA"),
    PLATA_II    (700,  799,  "Plata II",     "PLATA"),
    PLATA_III     (800,  899,  "Plata III",      "PLATA"),

    ORO_I     (900,  999,  "Oro I",      "ORO"),
    ORO_II      (1000, 1099, "Oro II",       "ORO"),
    ORO_III       (1100, 1199, "Oro III",        "ORO"),

    DIAMANTE_I(1200, 1299, "Diamante I", "DIAMANTE"),
    DIAMANTE_II (1300, 1399, "Diamante II",  "DIAMANTE"),
    DIAMANTE_III  (1400, 1499, "Diamante III",   "DIAMANTE"),

    PLATINO     (1500, Integer.MAX_VALUE, "Platino", "PLATINO");

    private final int    minPoints;
    private final int    maxPoints;
    private final String displayName; 
    private final String tier;       

    public static Rank fromPoints(int points) {
        Rank[] values = values();
        for (int i = values.length - 1; i >= 0; i--) {
            if (points >= values[i].minPoints) {
                return values[i];
            }
        }
        return HIERRO_I;
    }

    public boolean isSameTier(Rank other) {
        return this.tier.equals(other.tier);
    }
}