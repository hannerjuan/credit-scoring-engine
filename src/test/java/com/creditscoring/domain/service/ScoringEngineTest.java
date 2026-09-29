package com.creditscoring.domain.service;

import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleContextTestFactory;
import com.creditscoring.domain.rules.RiskRuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Prueba el ScoringEngine de forma totalmente aislada de las reglas
 * concretas, usando Mockito para simular RiskRule (Strategy). Esto
 * demuestra que el motor central es agnostico a la logica de negocio de
 * cada politica: solo agrega resultados.
 */
@ExtendWith(MockitoExtension.class)
class ScoringEngineTest {

    @Mock
    private RiskRule reglaA;

    @Mock
    private RiskRule reglaB;

    @Mock
    private RiskRule reglaInactiva;

    private RiskRuleContext context;

    @BeforeEach
    void setUp() {
        context = RiskRuleContextTestFactory.contextoConDti(java.math.BigDecimal.valueOf(20));
    }

    @Test
    @DisplayName("Suma los puntos de todas las reglas activas")
    void sumaPuntosDeTodasLasReglasActivas() {
        when(reglaA.isActiva()).thenReturn(true);
        when(reglaA.evaluate(context)).thenReturn(RiskRuleResult.puntaje("REGLA_A", 100));
        when(reglaB.isActiva()).thenReturn(true);
        when(reglaB.evaluate(context)).thenReturn(RiskRuleResult.puntaje("REGLA_B", 50));

        ScoringEngine engine = new ScoringEngine(List.of(reglaA, reglaB));
        EvaluacionScoring resultado = engine.evaluar(context);

        assertThat(resultado.getScoreTotal()).isEqualTo(150);
        assertThat(resultado.tieneCausalDeRechazo()).isFalse();
        verify(reglaA).evaluate(context);
        verify(reglaB).evaluate(context);
    }

    @Test
    @DisplayName("Ignora por completo las reglas desactivadas: nunca invoca evaluate() sobre ellas")
    void ignoraReglasDesactivadas() {
        when(reglaA.isActiva()).thenReturn(true);
        when(reglaA.evaluate(context)).thenReturn(RiskRuleResult.puntaje("REGLA_A", 100));
        when(reglaInactiva.isActiva()).thenReturn(false);

        ScoringEngine engine = new ScoringEngine(List.of(reglaA, reglaInactiva));
        EvaluacionScoring resultado = engine.evaluar(context);

        assertThat(resultado.getScoreTotal()).isEqualTo(100);
        verify(reglaInactiva, never()).evaluate(any());
    }

    @Test
    @DisplayName("Propaga los motivos de rechazo generados por cualquier regla y marca tieneCausalDeRechazo()")
    void propagaMotivosDeRechazo() {
        MotivoEvaluacion motivoRechazo = new MotivoEvaluacion("X", "motivo de prueba", MotivoEvaluacion.Tipo.RECHAZO, "REGLA_A");
        when(reglaA.isActiva()).thenReturn(true);
        when(reglaA.evaluate(context)).thenReturn(RiskRuleResult.rechazo("REGLA_A", motivoRechazo));
        when(reglaB.isActiva()).thenReturn(true);
        when(reglaB.evaluate(context)).thenReturn(RiskRuleResult.puntaje("REGLA_B", 300));

        ScoringEngine engine = new ScoringEngine(List.of(reglaA, reglaB));
        EvaluacionScoring resultado = engine.evaluar(context);

        assertThat(resultado.tieneCausalDeRechazo()).isTrue();
        assertThat(resultado.getMotivosRechazo()).containsExactly(motivoRechazo);
        // La regla de rechazo aporta 0 puntos; el score sigue acumulando lo de las demas reglas.
        assertThat(resultado.getScoreTotal()).isEqualTo(300);
    }

    @Test
    @DisplayName("El detalle de puntaje por regla queda disponible para trazabilidad/auditoria")
    void exponeDetallePorRegla() {
        when(reglaA.isActiva()).thenReturn(true);
        when(reglaA.evaluate(context)).thenReturn(RiskRuleResult.puntaje("REGLA_A", 75));

        ScoringEngine engine = new ScoringEngine(List.of(reglaA));
        EvaluacionScoring resultado = engine.evaluar(context);

        assertThat(resultado.getDetallePuntajePorRegla()).containsEntry("REGLA_A", 75);
    }

    @Test
    @DisplayName("Una lista vacia de reglas produce score cero sin causales de rechazo")
    void listaVaciaProduceScoreCero() {
        ScoringEngine engine = new ScoringEngine(List.of());
        EvaluacionScoring resultado = engine.evaluar(context);

        assertThat(resultado.getScoreTotal()).isZero();
        assertThat(resultado.tieneCausalDeRechazo()).isFalse();
    }
}
