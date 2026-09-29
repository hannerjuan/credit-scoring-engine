package com.creditscoring.domain.model;

import java.time.LocalDateTime;

/**
 * Value object inmutable con la respuesta simulada de la central de riesgo.
 * scoreBuro esta acotado entre 150 y 950 puntos, tal como lo especifica el
 * requerimiento funcional de simulacion de buro crediticio.
 */
public final class BuroCreditoResultado {

    public static final int SCORE_MIN = 150;
    public static final int SCORE_MAX = 950;

    private final CalificacionBuro calificacion;
    private final int scoreBuro;
    private final int numeroMorasActivas;
    private final LocalDateTime fechaConsulta;

    public BuroCreditoResultado(CalificacionBuro calificacion, int scoreBuro, int numeroMorasActivas,
                                 LocalDateTime fechaConsulta) {
        if (scoreBuro < SCORE_MIN || scoreBuro > SCORE_MAX) {
            throw new IllegalArgumentException(
                    "scoreBuro debe estar entre " + SCORE_MIN + " y " + SCORE_MAX + ", recibido: " + scoreBuro);
        }
        this.calificacion = calificacion;
        this.scoreBuro = scoreBuro;
        this.numeroMorasActivas = numeroMorasActivas;
        this.fechaConsulta = fechaConsulta;
    }

    public boolean tieneMorasActivas() {
        return numeroMorasActivas > 0;
    }

    public CalificacionBuro getCalificacion() { return calificacion; }
    public int getScoreBuro() { return scoreBuro; }
    public int getNumeroMorasActivas() { return numeroMorasActivas; }
    public LocalDateTime getFechaConsulta() { return fechaConsulta; }
}
