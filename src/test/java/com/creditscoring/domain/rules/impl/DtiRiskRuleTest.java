package com.creditscoring.domain.rules.impl;

import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleContextTestFactory;
import com.creditscoring.domain.rules.RiskRuleResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Casos de borde exigidos por el enunciado: clientes justo en el limite de
 * los cortes de DTI (35% y 45%).
 */
class DtiRiskRuleTest {

    private final DtiRiskRule rule = new DtiRiskRule(); // umbrales por defecto: 35 / 45

    @ParameterizedTest(name = "DTI={0}% => puntos={1}, causalRechazo={2}")
    @DisplayName("Matriz de puntos de DTI, incluyendo los bordes exactos 35.00% y 45.00%")
    @CsvSource({
            "0.00,   300, false",
            "34.99,  300, false",
            "35.00,  300, false",   // borde: <= 35 sigue siendo optimo
            "35.01,  150, false",
            "45.00,  150, false",   // borde: <= 45 todavia no es causal de rechazo
            "45.01,  0,   true",
            "60.00,  0,   true"
    })
    void evaluaSegunUmbralesDeDti(String dti, int puntosEsperados, boolean causalRechazoEsperada) {
        RiskRuleContext context = RiskRuleContextTestFactory.contextoConDti(new BigDecimal(dti));

        RiskRuleResult resultado = rule.evaluate(context);

        assertThat(resultado.getPuntos()).isEqualTo(puntosEsperados);
        assertThat(resultado.isCausalRechazo()).isEqualTo(causalRechazoEsperada);
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Cuando el DTI excede el maximo, el motivo generado es de tipo RECHAZO")
    void generaMotivoDeRechazoCuandoDtiExcedeElMaximo() {
        RiskRuleContext context = RiskRuleContextTestFactory.contextoConDti(BigDecimal.valueOf(50));
        RiskRuleResult resultado = rule.evaluate(context);

        assertThat(resultado.getMotivo()).isNotNull();
        assertThat(resultado.getMotivo().getTipo()).isEqualTo(MotivoEvaluacion.Tipo.RECHAZO);
        assertThat(resultado.getMotivo().getCodigo()).isEqualTo("DTI_EXCEDIDO");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Una regla desactivada no impide que se pueda evaluar, pero isActiva() lo refleja")
    void reglaPuedeDesactivarseSinModificarElMotorCentral() {
        DtiRiskRule reglaDesactivada = new DtiRiskRule(BigDecimal.valueOf(35), BigDecimal.valueOf(45), 300, 150, false);
        assertThat(reglaDesactivada.isActiva()).isFalse();
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Los umbrales son ajustables sin tocar la clase (ej: politica 40/50 en lugar de 35/45)")
    void permiteAjustarUmbralesSinModificarElMotor() {
        DtiRiskRule reglaAjustada = new DtiRiskRule(BigDecimal.valueOf(40), BigDecimal.valueOf(50), 300, 150, true);
        RiskRuleContext context = RiskRuleContextTestFactory.contextoConDti(BigDecimal.valueOf(42));

        RiskRuleResult resultado = reglaAjustada.evaluate(context);

        assertThat(resultado.getPuntos()).isEqualTo(150);
        assertThat(resultado.isCausalRechazo()).isFalse();
    }
}
