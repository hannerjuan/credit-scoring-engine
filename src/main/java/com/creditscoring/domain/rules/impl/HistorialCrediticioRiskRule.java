package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

/** Puntaje por la calificacion cualitativa (A/B/C) del historial de pagos reportado por el buro. */
public class HistorialCrediticioRiskRule implements RiskRule {

    public static final String NOMBRE = "HISTORIAL_CREDITICIO";

    private final boolean activa;

    public HistorialCrediticioRiskRule() { this(true); }

    public HistorialCrediticioRiskRule(boolean activa) { this.activa = activa; }

    @Override
    public RiskRuleResult evaluate(RiskRuleContext context) {
        int puntos = switch (context.getBuro().getCalificacion()) {
            case A -> 150;
            case B -> 90;
            case C -> 30;
        };
        return RiskRuleResult.puntaje(NOMBRE, puntos);
    }

    @Override
    public String getNombre() { return NOMBRE; }

    @Override
    public boolean isActiva() { return activa; }
}
