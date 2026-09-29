package com.creditscoring.domain.specification;

import com.creditscoring.domain.model.Dictamen;

import java.math.BigDecimal;

/**
 * APROBADO: score total supera el corte de aprobacion Y el DTI se mantiene
 * dentro del limite maximo aceptable, y no existe ninguna causal de rechazo.
 */
public class AprobadoSpecification implements DictamenSpecification {

    private final int scoreCorteAprobacion;
    private final BigDecimal dtiMaximoAprobacion;

    public AprobadoSpecification(int scoreCorteAprobacion, BigDecimal dtiMaximoAprobacion) {
        this.scoreCorteAprobacion = scoreCorteAprobacion;
        this.dtiMaximoAprobacion = dtiMaximoAprobacion;
    }

    @Override
    public boolean isSatisfiedBy(EvaluacionContext context) {
        if (context.isTieneCausalDeRechazo()) {
            return false;
        }
        return context.getScoreObtenido() >= scoreCorteAprobacion
                && context.getDtiCalculado().compareTo(dtiMaximoAprobacion) <= 0;
    }

    @Override
    public String getNombre() { return Dictamen.APROBADO.name(); }
}
