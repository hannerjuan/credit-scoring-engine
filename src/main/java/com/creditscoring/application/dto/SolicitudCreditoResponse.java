package com.creditscoring.application.dto;

import com.creditscoring.domain.model.EstadoSolicitud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SolicitudCreditoResponse {

    private Long id;
    private String numeroDocumento;
    private BigDecimal montoSolicitado;
    private int plazoMeses;
    private EstadoSolicitud estado;
    private LocalDateTime fechaSolicitud;

    public SolicitudCreditoResponse(Long id, String numeroDocumento, BigDecimal montoSolicitado, int plazoMeses,
                                     EstadoSolicitud estado, LocalDateTime fechaSolicitud) {
        this.id = id;
        this.numeroDocumento = numeroDocumento;
        this.montoSolicitado = montoSolicitado;
        this.plazoMeses = plazoMeses;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
    }

    public Long getId() { return id; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public int getPlazoMeses() { return plazoMeses; }
    public EstadoSolicitud getEstado() { return estado; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
}
