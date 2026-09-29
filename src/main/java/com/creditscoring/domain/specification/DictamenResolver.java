package com.creditscoring.domain.specification;

import com.creditscoring.domain.model.Dictamen;

import java.util.List;

/**
 * Resuelve el dictamen final evaluando las Specification en orden de
 * prioridad: Rechazado primero (causales duras tienen veto absoluto),
 * luego Aprobado, y por defecto Revision Manual. Anadir una nueva
 * Specification no requiere modificar esta clase mas alla de la lista
 * de construccion.
 */
public class DictamenResolver {

    private final List<DictamenSpecification> especificacionesEnOrden;

    public DictamenResolver(RechazadoSpecification rechazado, AprobadoSpecification aprobado,
                             RevisionManualSpecification revisionManual) {
        // El orden es semanticamente significativo: rechazo > aprobado > revision manual.
        this.especificacionesEnOrden = List.of(rechazado, aprobado, revisionManual);
    }

    public Dictamen resolver(EvaluacionContext context) {
        for (DictamenSpecification spec : especificacionesEnOrden) {
            if (spec.isSatisfiedBy(context)) {
                return Dictamen.valueOf(spec.getNombre());
            }
        }
        // Fallback defensivo; RevisionManualSpecification siempre deberia cubrir este caso.
        return Dictamen.REVISION_MANUAL;
    }
}
