package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.BuroCreditoResultado;
import com.creditscoring.domain.model.CalificacionBuro;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleContextTestFactory;
import com.creditscoring.domain.rules.RiskRuleResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MoraActivaRiskRuleTest {

    private final MoraActivaRiskRule rule = new MoraActivaRiskRule();

    @Test
    @DisplayName("Sin moras activas, la regla no genera causal de rechazo")
    void sinMorasActivasNoHayRechazo() {
        BuroCreditoResultado buro = new BuroCreditoResultado(CalificacionBuro.A, 800, 0, LocalDateTime.now());
        RiskRuleResult resultado = rule.evaluate(RiskRuleContextTestFactory.contextoConBuro(buro));

        assertThat(resultado.isCausalRechazo()).isFalse();
        assertThat(resultado.getMotivo()).isNull();
    }

    @ParameterizedTest(name = "numeroMorasActivas={0} => causal de rechazo")
    @DisplayName("Cualquier mora activa (>=1), sin importar la cantidad, es causal dura de rechazo")
    @ValueSource(ints = {1, 2, 5})
    void cualquierMoraActivaCausaRechazo(int numeroMoras) {
        BuroCreditoResultado buro = new BuroCreditoResultado(CalificacionBuro.A, 800, numeroMoras, LocalDateTime.now());
        RiskRuleResult resultado = rule.evaluate(RiskRuleContextTestFactory.contextoConBuro(buro));

        assertThat(resultado.isCausalRechazo()).isTrue();
        assertThat(resultado.getMotivo().getCodigo()).isEqualTo("MORA_ACTIVA_DETECTADA");
    }
}
