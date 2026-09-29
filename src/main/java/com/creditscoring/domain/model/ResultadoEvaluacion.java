package com.creditscoring.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Resultado inmutable de una evaluacion de riesgo. Se persiste tal cual para
 * cumplir el requerimiento de trazabilidad y auditoria: una vez creado nunca
 * se modifica, solo se lee.
 */
public final class ResultadoEvaluacion {

    private final Long id;
    private final Long idSolicitud;
    private final int scoreObtenido;
    private final BigDecimal dtiCalculado;
    private final BigDecimal cuotaEstimada;
    private final BigDecimal tasaInteresEA;
    private final Dictamen dictamen;
    private final List<MotivoEvaluacion> motivos;
    private final Map<String, Integer> detallePuntajePorRegla;
    private final String versionMotorReglas;
    private final LocalDateTime fechaEvaluacion;

    public ResultadoEvaluacion(Long id, Long idSolicitud, int scoreObtenido, BigDecimal dtiCalculado,
                                BigDecimal cuotaEstimada, BigDecimal tasaInteresEA, Dictamen dictamen,
                                List<MotivoEvaluacion> motivos, Map<String, Integer> detallePuntajePorRegla,
                                String versionMotorReglas, LocalDateTime fechaEvaluacion) {
        this.id = id;
        this.idSolicitud = idSolicitud;
        this.scoreObtenido = scoreObtenido;
        this.dtiCalculado = dtiCalculado;
        this.cuotaEstimada = cuotaEstimada;
        this.tasaInteresEA = tasaInteresEA;
        this.dictamen = dictamen;
        this.motivos = motivos == null ? Collections.emptyList() : List.copyOf(motivos);
        this.detallePuntajePorRegla = detallePuntajePorRegla == null ? Map.of() : Map.copyOf(detallePuntajePorRegla);
        this.versionMotorReglas = versionMotorReglas;
        this.fechaEvaluacion = fechaEvaluacion == null ? LocalDateTime.now() : fechaEvaluacion;
    }

    public Long getId() { return id; }
    public Long getIdSolicitud() { return idSolicitud; }
    public int getScoreObtenido() { return scoreObtenido; }
    public BigDecimal getDtiCalculado() { return dtiCalculado; }
    public BigDecimal getCuotaEstimada() { return cuotaEstimada; }
    public BigDecimal getTasaInteresEA() { return tasaInteresEA; }
    public Dictamen getDictamen() { return dictamen; }
    public List<MotivoEvaluacion> getMotivos() { return motivos; }
    public Map<String, Integer> getDetallePuntajePorRegla() { return detallePuntajePorRegla; }
    public String getVersionMotorReglas() { return versionMotorReglas; }
    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
}
