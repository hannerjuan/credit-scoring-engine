package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.BuroCreditoResultado;
import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

/**
 * Politica minima e inegociable: cualquier mora activa reportada por el buro
 * es causal directa de rechazo, sin importar el resto del puntaje.
 */
public class MoraActivaRiskRule implements RiskRule {

    public static final String NOMBRE = "MORA_ACTIVA";

    private final boolean activa;

    public MoraActivaRiskRule() { this(true); }

    public MoraActivaRiskRule(boolean activa) { this.activa = activa; }

    @Override
    public RiskRuleResult evaluate(RiskRuleContext context) {
        BuroCreditoResultado buro = context.getBuro();
        if (buro.tieneMorasActivas()) {
            MotivoEvaluacion motivo = new MotivoEvaluacion(
                    "MORA_ACTIVA_DETECTADA",
                    "El buro de credito reporta " + buro.getNumeroMorasActivas()
                            + " mora(s) activa(s), lo que incumple la politica minima de originacion.",
                    MotivoEvaluacion.Tipo.RECHAZO, NOMBRE);
            return RiskRuleResult.rechazo(NOMBRE, motivo);
        }
        return RiskRuleResult.puntaje(NOMBRE, 0);
    }

    @Override
    public String getNombre() { return NOMBRE; }

    @Override
    public boolean isActiva() { return activa; }
}
