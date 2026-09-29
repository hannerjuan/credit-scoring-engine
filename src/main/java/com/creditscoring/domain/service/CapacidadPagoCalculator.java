package com.creditscoring.domain.service;

import java.math.BigDecimal;

/**
 * Calcula la capacidad de pago residual: ingreso neto menos gastos fijos y
 * cuota proyectada. Se usa para verificar que el cliente no caiga por debajo
 * de un umbral minimo de subsistencia tras asumir el nuevo credito.
 */
public class CapacidadPagoCalculator {

    /** Umbral de subsistencia por defecto (referencial, configurable por el llamador). */
    public static final BigDecimal UMBRAL_SUBSISTENCIA_DEFAULT = BigDecimal.valueOf(600_000);

    public BigDecimal calcularCapacidadResidual(BigDecimal ingresosMensuales, BigDecimal egresosFijos,
                                                 BigDecimal cuotaProyectada) {
        return ingresosMensuales.subtract(egresosFijos).subtract(cuotaProyectada);
    }

    public boolean superaUmbralSubsistencia(BigDecimal capacidadResidual, BigDecimal umbralSubsistencia) {
        return capacidadResidual.compareTo(umbralSubsistencia) >= 0;
    }
}
