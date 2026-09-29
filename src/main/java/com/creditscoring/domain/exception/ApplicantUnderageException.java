package com.creditscoring.domain.exception;

/** Se lanza cuando el solicitante no cumple la edad minima legal para adquirir un credito. */
public class ApplicantUnderageException extends RuntimeException {

    private final int edadMinimaRequerida;
    private final int edadActual;

    public ApplicantUnderageException(int edadMinimaRequerida, int edadActual) {
        super("El solicitante tiene " + edadActual + " anios; se requiere un minimo de "
                + edadMinimaRequerida + " anios para procesar la solicitud.");
        this.edadMinimaRequerida = edadMinimaRequerida;
        this.edadActual = edadActual;
    }

    public int getEdadMinimaRequerida() { return edadMinimaRequerida; }
    public int getEdadActual() { return edadActual; }
}
