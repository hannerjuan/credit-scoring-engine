package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.TipoContrato;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

/**
 * Puntaje por estabilidad laboral: contrato a termino indefinido pondera
 * mas que prestacion de servicios, y la antiguedad laboral suma puntos
 * adicionales dentro de cada categoria.
 */
public class EstabilidadLaboralRiskRule implements RiskRule {

    public static final String NOMBRE = "ESTABILIDAD_LABORAL";
    private static final int ANTIGUEDAD_MINIMA_BONO_MESES = 12;

    private final boolean activa;

    public EstabilidadLaboralRiskRule() { this(true); }

    public EstabilidadLaboralRiskRule(boolean activa) { this.activa = activa; }

    @Override
    public RiskRuleResult evaluate(RiskRuleContext context) {
        TipoContrato tipo = context.getCliente().getTipoContrato();
        int antiguedadMeses = context.getCliente().getAntiguedadMeses();

        int puntosBase = switch (tipo) {
            case INDEFINIDO -> 100;
            case TERMINO_FIJO -> 70;
            case PRESTACION_SERVICIOS -> 50;
            case INDEPENDIENTE_SIN_CONTRATO -> 20;
        };

        int bonoAntiguedad = antiguedadMeses >= ANTIGUEDAD_MINIMA_BONO_MESES ? 50 : 0;

        return RiskRuleResult.puntaje(NOMBRE, puntosBase + bonoAntiguedad);
    }

    @Override
    public String getNombre() { return NOMBRE; }

    @Override
    public boolean isActiva() { return activa; }
}
