package com.creditscoring.application.dto;

import com.creditscoring.domain.model.TipoContrato;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Contrato de entrada de POST /api/v1/credit-applications. Combina los
 * datos del cliente (documento, antiguedad, etc.) con las condiciones del
 * credito solicitado, tal como pide el requerimiento de "ingreso y
 * validacion de solicitudes".
 */
public class SolicitudCreditoRequest {

    @NotBlank(message = "numeroDocumento es obligatorio")
    private String numeroDocumento;

    @NotBlank(message = "nombreCompleto es obligatorio")
    private String nombreCompleto;

    @NotNull(message = "fechaNacimiento es obligatoria")
    @Past(message = "fechaNacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    @NotNull(message = "tipoContrato es obligatorio")
    private TipoContrato tipoContrato;

    @Min(value = 0, message = "antiguedadMeses no puede ser negativa")
    private int antiguedadMeses;

    @NotBlank(message = "actividadEconomica es obligatoria")
    private String actividadEconomica;

    @NotNull(message = "ingresosMensuales es obligatorio")
    @DecimalMin(value = "0.01", message = "ingresosMensuales debe ser mayor a cero")
    private BigDecimal ingresosMensuales;

    @NotNull(message = "egresosMensuales es obligatorio")
    @DecimalMin(value = "0.0", message = "egresosMensuales no puede ser negativo")
    private BigDecimal egresosMensuales;

    @NotNull(message = "montoSolicitado es obligatorio")
    @DecimalMin(value = "1.0", message = "montoSolicitado debe ser mayor a cero")
    private BigDecimal montoSolicitado;

    @Min(value = 1, message = "plazoMeses debe ser al menos 1")
    @Max(value = 360, message = "plazoMeses no puede superar 360")
    private int plazoMeses;

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public TipoContrato getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(TipoContrato tipoContrato) { this.tipoContrato = tipoContrato; }

    public int getAntiguedadMeses() { return antiguedadMeses; }
    public void setAntiguedadMeses(int antiguedadMeses) { this.antiguedadMeses = antiguedadMeses; }

    public String getActividadEconomica() { return actividadEconomica; }
    public void setActividadEconomica(String actividadEconomica) { this.actividadEconomica = actividadEconomica; }

    public BigDecimal getIngresosMensuales() { return ingresosMensuales; }
    public void setIngresosMensuales(BigDecimal ingresosMensuales) { this.ingresosMensuales = ingresosMensuales; }

    public BigDecimal getEgresosMensuales() { return egresosMensuales; }
    public void setEgresosMensuales(BigDecimal egresosMensuales) { this.egresosMensuales = egresosMensuales; }

    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public void setMontoSolicitado(BigDecimal montoSolicitado) { this.montoSolicitado = montoSolicitado; }

    public int getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(int plazoMeses) { this.plazoMeses = plazoMeses; }
}
