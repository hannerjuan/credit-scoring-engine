package com.creditscoring.domain.exception;

/** Se lanza al pedir la tabla de amortizacion de una solicitud que no quedo APROBADA. */
public class AmortizacionNoDisponibleException extends RuntimeException {

    public AmortizacionNoDisponibleException(Long idSolicitud, String dictamenActual) {
        super("La solicitud " + idSolicitud + " no tiene amortizacion disponible porque su dictamen es "
                + dictamenActual + " (se requiere APROBADO).");
    }
}
