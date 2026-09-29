package com.creditscoring.domain.model;

import java.math.BigDecimal;
import java.util.List;

public final class TablaAmortizacion {

    private final Long idSolicitud;
    private final BigDecimal montoSolicitado;
    private final BigDecimal tasaInteresEA;
    private final BigDecimal tasaInteresMensual;
    private final int plazoMeses;
    private final List<CuotaAmortizacion> cuotas;

    public TablaAmortizacion(Long idSolicitud, BigDecimal montoSolicitado, BigDecimal tasaInteresEA,
                              BigDecimal tasaInteresMensual, int plazoMeses, List<CuotaAmortizacion> cuotas) {
        this.idSolicitud = idSolicitud;
        this.montoSolicitado = montoSolicitado;
        this.tasaInteresEA = tasaInteresEA;
        this.tasaInteresMensual = tasaInteresMensual;
        this.plazoMeses = plazoMeses;
        this.cuotas = List.copyOf(cuotas);
    }

    public Long getIdSolicitud() { return idSolicitud; }
    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public BigDecimal getTasaInteresEA() { return tasaInteresEA; }
    public BigDecimal getTasaInteresMensual() { return tasaInteresMensual; }
    public int getPlazoMeses() { return plazoMeses; }
    public List<CuotaAmortizacion> getCuotas() { return cuotas; }
}
