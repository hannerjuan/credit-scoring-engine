package com.creditscoring.domain.specification;

import java.math.BigDecimal;

/** Datos minimos que necesitan las Specification para decidir el dictamen final. */
public final class EvaluacionContext {

    private final int scoreObtenido;
    private final BigDecimal dtiCalculado;
    private final boolean tieneCausalDeRechazo;

    public EvaluacionContext(int scoreObtenido, BigDecimal dtiCalculado, boolean tieneCausalDeRechazo) {
        this.scoreObtenido = scoreObtenido;
        this.dtiCalculado = dtiCalculado;
        this.tieneCausalDeRechazo = tieneCausalDeRechazo;
    }

    public int getScoreObtenido() { return scoreObtenido; }
    public BigDecimal getDtiCalculado() { return dtiCalculado; }
    public boolean isTieneCausalDeRechazo() { return tieneCausalDeRechazo; }
}
