package com.creditscoring.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class SolicitudCredito {

    private final Long id;
    private final Cliente cliente;
    private final BigDecimal montoSolicitado;
    private final int plazoMeses;
    private final BigDecimal ingresosMensuales;
    private final BigDecimal egresosMensuales;
    private EstadoSolicitud estado;
    private final LocalDateTime fechaSolicitud;

    public SolicitudCredito(Long id, Cliente cliente, BigDecimal montoSolicitado, int plazoMeses,
                             BigDecimal ingresosMensuales, BigDecimal egresosMensuales,
                             EstadoSolicitud estado, LocalDateTime fechaSolicitud) {
        this.id = id;
        this.cliente = Objects.requireNonNull(cliente, "cliente es obligatorio");
        this.montoSolicitado = Objects.requireNonNull(montoSolicitado, "montoSolicitado es obligatorio");
        this.plazoMeses = plazoMeses;
        this.ingresosMensuales = Objects.requireNonNull(ingresosMensuales, "ingresosMensuales es obligatorio");
        this.egresosMensuales = Objects.requireNonNull(egresosMensuales, "egresosMensuales es obligatorio");
        this.estado = estado == null ? EstadoSolicitud.PENDIENTE : estado;
        this.fechaSolicitud = fechaSolicitud == null ? LocalDateTime.now() : fechaSolicitud;
    }

    public void marcarComoEvaluada() {
        this.estado = EstadoSolicitud.EVALUADA;
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public int getPlazoMeses() { return plazoMeses; }
    public BigDecimal getIngresosMensuales() { return ingresosMensuales; }
    public BigDecimal getEgresosMensuales() { return egresosMensuales; }
    public EstadoSolicitud getEstado() { return estado; }
    public LocalDateTime getFechaSolicitud() { return fechaSolicitud; }
}
