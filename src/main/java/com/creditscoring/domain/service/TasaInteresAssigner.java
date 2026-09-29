package com.creditscoring.domain.service;

import com.creditscoring.domain.model.CalificacionBuro;

import java.math.BigDecimal;

/**
 * Asigna la tasa de interes efectiva anual (EA) preferencial para
 * solicitudes APROBADAS, en funcion de la calificacion cualitativa del
 * buro. Las tasas de evaluacion (usadas para proyectar el DTI antes de
 * conocer el dictamen) se manejan aparte, de forma conservadora.
 */
public class TasaInteresAssigner {

    /** Tasa de referencia conservadora usada SOLO para proyectar la cuota durante la evaluacion de riesgo. */
    public static final BigDecimal TASA_EVALUACION_EA = BigDecimal.valueOf(0.24); // 24% EA

    public BigDecimal asignarTasaPreferencial(CalificacionBuro calificacion) {
        return switch (calificacion) {
            case A -> BigDecimal.valueOf(0.12); // 12% EA
            case B -> BigDecimal.valueOf(0.18); // 18% EA
            case C -> BigDecimal.valueOf(0.22); // 22% EA
        };
    }
}
