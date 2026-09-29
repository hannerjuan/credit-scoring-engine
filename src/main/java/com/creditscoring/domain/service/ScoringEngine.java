package com.creditscoring.domain.service;

import com.creditscoring.domain.model.MotivoEvaluacion;
import com.creditscoring.domain.rules.RiskRule;
import com.creditscoring.domain.rules.RiskRuleContext;
import com.creditscoring.domain.rules.RiskRuleResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Motor central de scoring. Deliberadamente NO contiene ninguna regla de
 * negocio ni bloques if-else de politicas: solo sabe iterar una lista de
 * RiskRule (Strategy) y agregar sus resultados. Anadir, quitar, desactivar o
 * reponderar una politica de riesgo nunca requiere tocar esta clase.
 *
 * POJO puro de dominio, sin dependencias de Spring ni de persistencia.
 */
public class ScoringEngine {

    private final List<RiskRule> reglas;

    public ScoringEngine(List<RiskRule> reglas) {
        this.reglas = List.copyOf(reglas);
    }

    public EvaluacionScoring evaluar(RiskRuleContext context) {
        int scoreTotal = 0;
        List<MotivoEvaluacion> motivos = new ArrayList<>();
        Map<String, Integer> detallePorRegla = new LinkedHashMap<>();

        for (RiskRule regla : reglas) {
            if (!regla.isActiva()) {
                continue;
            }
            RiskRuleResult resultado = regla.evaluate(context);
            detallePorRegla.put(resultado.getNombreRegla(), resultado.getPuntos());
            scoreTotal += resultado.getPuntos();

            if (resultado.getMotivo() != null) {
                motivos.add(resultado.getMotivo());
            }
        }

        return new EvaluacionScoring(scoreTotal, motivos, detallePorRegla);
    }
}
