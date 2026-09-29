package com.creditscoring.domain.specification;

import com.creditscoring.domain.model.Dictamen;

/**
 * REVISION_MANUAL: zona gris. No cumple ninguna causal de rechazo, pero
 * tampoco alcanza el puntaje/DTI necesarios para aprobacion automatica.
 * Se evalua como fallback despues de Rechazado y Aprobado.
 */
public class RevisionManualSpecification implements DictamenSpecification {

    @Override
    public boolean isSatisfiedBy(EvaluacionContext context) {
        return !context.isTieneCausalDeRechazo();
    }

    @Override
    public String getNombre() { return Dictamen.REVISION_MANUAL.name(); }
}
