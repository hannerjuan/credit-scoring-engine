package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

import java.math.BigDecimal;

/** Puntaje adicional por nivel absoluto de ingresos mensuales certificados. */
public class NivelIngresosRiskRule implements RiskRule {

    public static final String NOMBRE = "NIVEL_INGRESOS";

    private final BigDecimal umbralAlto;
    private final BigDecimal umbralMedio;
    private final boolean activa;

    public NivelIngresosRiskRule() {
        this(BigDecimal.valueOf(5_000_000), BigDecimal.valueOf(2_000_000), true);
    }

    public NivelIngresosRiskRule(BigDecimal umbralAlto, BigDecimal umbralMedio, boolean activa) {
        this.umbralAlto = umbralAlto;
        this.umbralMedio = umbralMedio;
        this.activa = activa;
    }

    @Override
    public RiskRuleResult evaluate(RiskRuleContext context) {
        BigDecimal ingresos = context.getSolicitud().getIngresosMensuales();

        int puntos;
        if (ingresos.compareTo(umbralAlto) >= 0) {
            puntos = 100;
        } else if (ingresos.compareTo(umbralMedio) >= 0) {
            puntos = 60;
        } else {
            puntos = 20;
        }
        return RiskRuleResult.puntaje(NOMBRE, puntos);
    }

    @Override
    public String getNombre() { return NOMBRE; }

    @Override
    public boolean isActiva() { return activa; }
}
