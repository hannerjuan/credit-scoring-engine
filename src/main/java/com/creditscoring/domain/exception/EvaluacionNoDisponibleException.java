package com.creditscoring.domain.exception;

/** Se lanza al consultar el dictamen o la amortizacion de una solicitud que aun no ha sido evaluada. */
public class EvaluacionNoDisponibleException extends RuntimeException {

    public EvaluacionNoDisponibleException(Long idSolicitud) {
        super("La solicitud " + idSolicitud + " todavia no ha sido evaluada.");
    }
}
