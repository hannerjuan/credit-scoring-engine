package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleContextTestFactory;
import com.creditscoring.domain.rules.RiskRuleResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class EdadMinimaRiskRuleTest {

    private final EdadMinimaRiskRule rule = new EdadMinimaRiskRule(); // edad minima por defecto: 18

    @ParameterizedTest(name = "edad={0} => causalRechazo={1}")
    @DisplayName("Borde exacto de la edad minima legal (18 anios)")
    @CsvSource({
            "17, true",
            "18, false", // borde: cumple exactamente la mayoria de edad
            "19, false",
            "65, false"
    })
    void evaluaEdadMinima(int edad, boolean causalRechazoEsperada) {
        RiskRuleContext context = RiskRuleContextTestFactory.contextoConEdad(edad);
        RiskRuleResult resultado = rule.evaluate(context);

        assertThat(resultado.isCausalRechazo()).isEqualTo(causalRechazoEsperada);
    }
}
