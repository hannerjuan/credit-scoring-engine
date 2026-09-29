package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleContextTestFactory;
import com.creditscoring.domain.rules.RiskRuleResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CapacidadPagoResidualRiskRuleTest {

    private final CapacidadPagoResidualRiskRule rule = new CapacidadPagoResidualRiskRule();

    @ParameterizedTest(name = "residual={0}, umbral={1} => causalRechazo={2}")
    @DisplayName("Borde exacto del umbral minimo de subsistencia")
    @CsvSource({
            "600000, 600000, false",  // exactamente en el umbral: no es causal de rechazo
            "599999, 600000, true",   // un peso por debajo: si es causal
            "1000000, 600000, false",
            "0, 600000, true"
    })
    void evaluaCapacidadResidual(String residual, String umbral, boolean causalRechazoEsperada) {
        RiskRuleContext context = RiskRuleContextTestFactory.contextoConCapacidadResidual(
                new BigDecimal(residual), new BigDecimal(umbral));

        RiskRuleResult resultado = rule.evaluate(context);

        assertThat(resultado.isCausalRechazo()).isEqualTo(causalRechazoEsperada);
    }
}
