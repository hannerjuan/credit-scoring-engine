package com.creditscoring.domain.model;

/**
 * Tipo de vinculacion laboral del cliente. Afecta directamente el puntaje
 * de estabilidad laboral dentro de la matriz de ponderacion.
 */
public enum TipoContrato {
    INDEFINIDO,
    TERMINO_FIJO,
    PRESTACION_SERVICIOS,
    INDEPENDIENTE_SIN_CONTRATO
}
