package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.BuroCreditoResultado;
import com.creditscoring.domain.model.CalificacionBuro;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleContextTestFactory;
import com.creditscoring.domain.rules.RiskRuleResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreBuroRiskRuleTest {

    private final ScoreBuroRiskRule rule = new ScoreBuroRiskRule();

    @ParameterizedTest(name = "score={0} => puntos={1}, causalRechazo={2}")
    @DisplayName("Matriz de puntos por franja de score de buro, incluyendo bordes 400/600/700")
    @CsvSource({
            "150, 0,   true",   // score minimo absoluto del buro
            "399, 0,   true",
            "400, 50,  false",  // borde inferior de la franja moderada
            "599, 50,  false",
            "600, 150, false",  // borde inferior de la franja buena
            "699, 150, false",
            "700, 300, false",  // borde inferior de la franja excelente
            "950, 300, false"   // score maximo absoluto del buro
    })
    void evaluaSegunFranjaDeScore(int score, int puntosEsperados, boolean causalRechazoEsperada) {
        BuroCreditoResultado buro = new BuroCreditoResultado(CalificacionBuro.B, score, 0, LocalDateTime.now());
        RiskRuleContext context = RiskRuleContextTestFactory.contextoConBuro(buro);

        RiskRuleResult resultado = rule.evaluate(context);

        assertThat(resultado.getPuntos()).isEqualTo(puntosEsperados);
        assertThat(resultado.isCausalRechazo()).isEqualTo(causalRechazoEsperada);
    }
}
