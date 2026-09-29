package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.TipoContrato;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleContextTestFactory;
import com.creditscoring.domain.rules.RiskRuleResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class EstabilidadLaboralRiskRuleTest {

    private final EstabilidadLaboralRiskRule rule = new EstabilidadLaboralRiskRule();

    @ParameterizedTest(name = "tipoContrato={0}, antiguedadMeses={1} => puntos={2}")
    @DisplayName("Puntaje combinado de tipo de contrato + bono de antiguedad (borde exacto: 12 meses)")
    @CsvSource({
            "INDEFINIDO, 0, 100",
            "INDEFINIDO, 11, 100",
            "INDEFINIDO, 12, 150",   // borde exacto del bono de antiguedad
            "TERMINO_FIJO, 12, 120",
            "PRESTACION_SERVICIOS, 12, 100",
            "PRESTACION_SERVICIOS, 5, 50",
            "INDEPENDIENTE_SIN_CONTRATO, 0, 20"
    })
    void calculaPuntajeDeEstabilidad(TipoContrato tipoContrato, int antiguedadMeses, int puntosEsperados) {
        RiskRuleContext context = RiskRuleContextTestFactory.contextoConTipoContratoYAntiguedad(tipoContrato, antiguedadMeses);

        RiskRuleResult resultado = rule.evaluate(context);

        assertThat(resultado.getPuntos()).isEqualTo(puntosEsperados);
        assertThat(resultado.isCausalRechazo()).isFalse();
    }
}
