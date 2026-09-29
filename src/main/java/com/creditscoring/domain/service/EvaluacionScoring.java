package com.creditscoring.domain.service;

import com.creditscoring.domain.model.MotivoEvaluacion;

import java.util.List;
import java.util.Map;

/** Resultado agregado de correr todas las RiskRule activas sobre un contexto. */
public final class EvaluacionScoring {

    private final int scoreTotal;
    private final List<MotivoEvaluacion> motivos; // incluye RECHAZO y ALERTA
    private final Map<String, Integer> detallePuntajePorRegla;

    public EvaluacionScoring(int scoreTotal, List<MotivoEvaluacion> motivos,
                              Map<String, Integer> detallePuntajePorRegla) {
        this.scoreTotal = scoreTotal;
        this.motivos = List.copyOf(motivos);
        this.detallePuntajePorRegla = Map.copyOf(detallePuntajePorRegla);
    }

    public boolean tieneCausalDeRechazo() {
        return motivos.stream().anyMatch(m -> m.getTipo() == MotivoEvaluacion.Tipo.RECHAZO);
    }

    public List<MotivoEvaluacion> getMotivosRechazo() {
        return motivos.stream().filter(m -> m.getTipo() == MotivoEvaluacion.Tipo.RECHAZO).toList();
    }

    public int getScoreTotal() { return scoreTotal; }
    public List<MotivoEvaluacion> getMotivos() { return motivos; }
    public Map<String, Integer> getDetallePuntajePorRegla() { return detallePuntajePorRegla; }
}
