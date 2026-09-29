package com.creditscoring.application.dto;

import com.creditscoring.domain.model.Dictamen;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EvaluacionResponse {

    public static class MotivoResponse {
        private final String codigo;
        private final String descripcion;
        private final String tipo;

        public MotivoResponse(String codigo, String descripcion, String tipo) {
            this.codigo = codigo;
            this.descripcion = descripcion;
            this.tipo = tipo;
        }

        public String getCodigo() { return codigo; }
        public String getDescripcion() { return descripcion; }
        public String getTipo() { return tipo; }
    }

    private Long idSolicitud;
    private int scoreObtenido;
    private BigDecimal dtiCalculado;
    private BigDecimal cuotaEstimada;
    private BigDecimal tasaInteresEA;
    private Dictamen dictamen;
    private List<MotivoResponse> motivos;
    private Map<String, Integer> detallePuntajePorRegla;
    private String versionMotorReglas;
    private LocalDateTime fechaEvaluacion;

    public EvaluacionResponse(Long idSolicitud, int scoreObtenido, BigDecimal dtiCalculado, BigDecimal cuotaEstimada,
                               BigDecimal tasaInteresEA, Dictamen dictamen, List<MotivoResponse> motivos,
                               Map<String, Integer> detallePuntajePorRegla, String versionMotorReglas,
                               LocalDateTime fechaEvaluacion) {
        this.idSolicitud = idSolicitud;
        this.scoreObtenido = scoreObtenido;
        this.dtiCalculado = dtiCalculado;
        this.cuotaEstimada = cuotaEstimada;
        this.tasaInteresEA = tasaInteresEA;
        this.dictamen = dictamen;
        this.motivos = motivos;
        this.detallePuntajePorRegla = detallePuntajePorRegla;
        this.versionMotorReglas = versionMotorReglas;
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public Long getIdSolicitud() { return idSolicitud; }
    public int getScoreObtenido() { return scoreObtenido; }
    public BigDecimal getDtiCalculado() { return dtiCalculado; }
    public BigDecimal getCuotaEstimada() { return cuotaEstimada; }
    public BigDecimal getTasaInteresEA() { return tasaInteresEA; }
    public Dictamen getDictamen() { return dictamen; }
    public List<MotivoResponse> getMotivos() { return motivos; }
    public Map<String, Integer> getDetallePuntajePorRegla() { return detallePuntajePorRegla; }
    public String getVersionMotorReglas() { return versionMotorReglas; }
    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
}
