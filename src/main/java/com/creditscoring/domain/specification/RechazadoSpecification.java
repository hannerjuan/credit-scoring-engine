package com.creditscoring.domain.specification;

import com.creditscoring.domain.model.Dictamen;

/**
 * RECHAZADO: hay al menos una causal dura (mora activa, score insuficiente,
 * DTI excedido, capacidad de pago insuficiente, edad, etc.) o el score total
 * cae por debajo del minimo absoluto.
 */
public class RechazadoSpecification implements DictamenSpecification {

    private final int scoreMinimoAbsoluto;

    public RechazadoSpecification(int scoreMinimoAbsoluto) {
        this.scoreMinimoAbsoluto = scoreMinimoAbsoluto;
    }

    @Override
    public boolean isSatisfiedBy(EvaluacionContext context) {
        return context.isTieneCausalDeRechazo() || context.getScoreObtenido() < scoreMinimoAbsoluto;
    }

    @Override
    public String getNombre() { return Dictamen.RECHAZADO.name(); }
}
