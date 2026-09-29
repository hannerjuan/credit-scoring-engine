package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

import java.math.BigDecimal;

/**
 * Regla de ponderacion por DTI (Debt-to-Income), segun la matriz base:
 *
 *   DTI <= 35%            -> +300 puntos
 *   35% < DTI <= 45%       -> +150 puntos
 *   DTI > 45%              -> 0 puntos y causal de rechazo / riesgo alto
 *
 * Los umbrales son parametros del constructor (no constantes fijas): esto
 * permite ajustar la politica (ej. usar 40%/50% en vez de 35%/45%) sin tocar
 * el ScoringEngine ni ninguna otra regla.
 */
public class DtiRiskRule implements RiskRule {

    public static final String NOMBRE = "DTI_RATIO";

    private final BigDecimal umbralOptimo;   // p.ej. 35
    private final BigDecimal umbralMaximo;   // p.ej. 45
    private final int puntosOptimo;          // p.ej. 300
    private final int puntosIntermedio;      // p.ej. 150
    private final boolean activa;

    public DtiRiskRule() {
        this(BigDecimal.valueOf(35), BigDecimal.valueOf(45), 300, 150, true);
    }

    public DtiRiskRule(BigDecimal umbralOptimo, BigDecimal umbralMaximo, int puntosOptimo,
                        int puntosIntermedio, boolean activa) {
        this.umbralOptimo = umbralOptimo;
        this.umbralMaximo = umbralMaximo;
        this.puntosOptimo = puntosOptimo;
        this.puntosIntermedio = puntosIntermedio;
        this.activa = activa;
    }

    @Override
    public RiskRuleResult evaluate(RiskRuleContext context) {
        BigDecimal dti = context.getDtiCalculado();

        if (dti.compareTo(umbralOptimo) <= 0) {
            return RiskRuleResult.puntaje(NOMBRE, puntosOptimo);
        }
        if (dti.compareTo(umbralMaximo) <= 0) {
            MotivoEvaluacion alerta = new MotivoEvaluacion(
                    "DTI_INTERMEDIO",
                    "El DTI (" + dti + "%) supera el umbral optimo de " + umbralOptimo
                            + "% pero se mantiene dentro del limite maximo de " + umbralMaximo + "%.",
                    MotivoEvaluacion.Tipo.ALERTA, NOMBRE);
            return RiskRuleResult.puntajeConAlerta(NOMBRE, puntosIntermedio, alerta);
        }

        MotivoEvaluacion motivoRechazo = new MotivoEvaluacion(
                "DTI_EXCEDIDO",
                "El DTI calculado (" + dti + "%) supera el umbral maximo permitido de " + umbralMaximo + "%.",
                MotivoEvaluacion.Tipo.RECHAZO, NOMBRE);
        return RiskRuleResult.rechazo(NOMBRE, motivoRechazo);
    }

    @Override
    public String getNombre() { return NOMBRE; }

    @Override
    public boolean isActiva() { return activa; }
}
