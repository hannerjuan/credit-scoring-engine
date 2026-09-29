package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

/**
 * Regla de edad minima como politica de riesgo configurable dentro del
 * motor (independiente de la validacion de entrada que lanza
 * ApplicantUnderageException de forma temprana en el flujo de registro).
 * Actua como defensa adicional dentro del propio ScoringEngine.
 */
public class EdadMinimaRiskRule implements RiskRule {

    public static final String NOMBRE = "EDAD_MINIMA";

    private final int edadMinima;
    private final boolean activa;

    public EdadMinimaRiskRule() { this(18, true); }

    public EdadMinimaRiskRule(int edadMinima, boolean activa) {
        this.edadMinima = edadMinima;
        this.activa = activa;
    }

    @Override
    public RiskRuleResult evaluate(RiskRuleContext context) {
        int edad = context.getEdadSolicitante();
        if (edad < edadMinima) {
            MotivoEvaluacion motivo = new MotivoEvaluacion(
                    "EDAD_INSUFICIENTE",
                    "El solicitante tiene " + edad + " anios; se requiere un minimo de " + edadMinima + ".",
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
