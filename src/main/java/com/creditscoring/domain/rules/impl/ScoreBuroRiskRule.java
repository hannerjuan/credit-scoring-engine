package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

/**
 * Asigna puntos de la matriz de ponderacion segun la franja del score
 * retornado por la central de riesgo (150-950).
 *
 *   score >= 700          -> +300
 *   600 <= score < 700     -> +150
 *   400 <= score < 600     -> +50
 *   score < 400            -> 0 puntos y causal de rechazo (score insuficiente)
 */
public class ScoreBuroRiskRule implements RiskRule {

    public static final String NOMBRE = "SCORE_BURO";

    private static final int UMBRAL_EXCELENTE = 700;
    private static final int UMBRAL_BUENO = 600;
    private static final int UMBRAL_MINIMO = 400;

    private final boolean activa;

    public ScoreBuroRiskRule() { this(true); }

    public ScoreBuroRiskRule(boolean activa) { this.activa = activa; }

    @Override
    public RiskRuleResult evaluate(RiskRuleContext context) {
        int score = context.getBuro().getScoreBuro();

        if (score >= UMBRAL_EXCELENTE) {
            return RiskRuleResult.puntaje(NOMBRE, 300);
        }
        if (score >= UMBRAL_BUENO) {
            return RiskRuleResult.puntaje(NOMBRE, 150);
        }
        if (score >= UMBRAL_MINIMO) {
            MotivoEvaluacion alerta = new MotivoEvaluacion(
                    "SCORE_BURO_MODERADO",
                    "El score de buro (" + score + ") esta en una franja moderada (400-599).",
                    MotivoEvaluacion.Tipo.ALERTA, NOMBRE);
            return RiskRuleResult.puntajeConAlerta(NOMBRE, 50, alerta);
        }

        MotivoEvaluacion motivoRechazo = new MotivoEvaluacion(
                "SCORE_BURO_INSUFICIENTE",
                "El score de buro (" + score + ") esta por debajo del minimo aceptable de " + UMBRAL_MINIMO + ".",
                MotivoEvaluacion.Tipo.RECHAZO, NOMBRE);
        return RiskRuleResult.rechazo(NOMBRE, motivoRechazo);
    }

    @Override
    public String getNombre() { return NOMBRE; }

    @Override
    public boolean isActiva() { return activa; }
}
