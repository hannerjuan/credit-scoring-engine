package com.creditscoring.domain.rules;

import com.creditscoring.domain.model.MotivoEvaluacion;

/**
 * Resultado de evaluar una unica RiskRule: cuantos puntos aporta a la matriz
 * de ponderacion y, opcionalmente, si constituye por si sola una causal dura
 * de rechazo (ej: mora activa, capacidad de pago insuficiente).
 */
public final class RiskRuleResult {

    private final String nombreRegla;
    private final int puntos;
    private final boolean causalRechazo;
    private final MotivoEvaluacion motivo; // puede ser null si la regla no genero alertas

    private RiskRuleResult(String nombreRegla, int puntos, boolean causalRechazo, MotivoEvaluacion motivo) {
        this.nombreRegla = nombreRegla;
        this.puntos = puntos;
        this.causalRechazo = causalRechazo;
        this.motivo = motivo;
    }

    public static RiskRuleResult puntaje(String nombreRegla, int puntos) {
        return new RiskRuleResult(nombreRegla, puntos, false, null);
    }

    public static RiskRuleResult puntajeConAlerta(String nombreRegla, int puntos, MotivoEvaluacion alerta) {
        return new RiskRuleResult(nombreRegla, puntos, false, alerta);
    }

    public static RiskRuleResult rechazo(String nombreRegla, MotivoEvaluacion motivoRechazo) {
        return new RiskRuleResult(nombreRegla, 0, true, motivoRechazo);
    }

    public String getNombreRegla() { return nombreRegla; }
    public int getPuntos() { return puntos; }
    public boolean isCausalRechazo() { return causalRechazo; }
    public MotivoEvaluacion getMotivo() { return motivo; }
}
