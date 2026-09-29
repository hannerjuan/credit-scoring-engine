package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

import java.math.BigDecimal;

/**
 * Verifica que, tras descontar egresos fijos y la cuota proyectada, el
 * ingreso residual del solicitante no caiga por debajo del umbral minimo de
 * subsistencia. Si cae, es causal dura de rechazo independiente del score.
 */
public class CapacidadPagoResidualRiskRule implements RiskRule {

    public static final String NOMBRE = "CAPACIDAD_PAGO_RESIDUAL";

    private final boolean activa;

    public CapacidadPagoResidualRiskRule() { this(true); }

    public CapacidadPagoResidualRiskRule(boolean activa) { this.activa = activa; }

    @Override
    public RiskRuleResult evaluate(RiskRuleContext context) {
        BigDecimal capacidadResidual = context.getCapacidadPagoResidual();
        BigDecimal umbral = context.getUmbralSubsistencia();

        if (capacidadResidual.compareTo(umbral) < 0) {
            MotivoEvaluacion motivo = new MotivoEvaluacion(
                    "CAPACIDAD_PAGO_INSUFICIENTE",
                    "El ingreso residual proyectado (" + capacidadResidual
                            + ") queda por debajo del umbral minimo de subsistencia (" + umbral + ").",
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
