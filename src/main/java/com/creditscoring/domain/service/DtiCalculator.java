package com.creditscoring.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula la relacion Deuda-Ingreso (DTI):
 *
 *   DTI = (Egresos Fijos + Cuota Proyectada) / Ingresos Mensuales * 100
 *
 * POJO puro, sin dependencias externas.
 */
public class DtiCalculator {

    private static final int ESCALA = 2;

    public BigDecimal calcular(BigDecimal ingresosMensuales, BigDecimal egresosFijos, BigDecimal cuotaProyectada) {
        if (ingresosMensuales == null || ingresosMensuales.signum() <= 0) {
            throw new IllegalArgumentException("ingresosMensuales debe ser mayor a cero para calcular el DTI");
        }
        BigDecimal totalCompromisos = egresosFijos.add(cuotaProyectada);
        return totalCompromisos
                .divide(ingresosMensuales, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(ESCALA, RoundingMode.HALF_UP);
    }
}
