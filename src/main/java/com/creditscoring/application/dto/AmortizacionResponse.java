package com.creditscoring.application.dto;

import java.math.BigDecimal;
import java.util.List;

public class AmortizacionResponse {

    public static class CuotaResponse {
        private final int numeroCuota;
        private final BigDecimal saldoInicial;
        private final BigDecimal cuota;
        private final BigDecimal interes;
        private final BigDecimal capital;
        private final BigDecimal saldoFinal;

        public CuotaResponse(int numeroCuota, BigDecimal saldoInicial, BigDecimal cuota, BigDecimal interes,
                              BigDecimal capital, BigDecimal saldoFinal) {
            this.numeroCuota = numeroCuota;
            this.saldoInicial = saldoInicial;
            this.cuota = cuota;
            this.interes = interes;
            this.capital = capital;
            this.saldoFinal = saldoFinal;
        }

        public int getNumeroCuota() { return numeroCuota; }
        public BigDecimal getSaldoInicial() { return saldoInicial; }
        public BigDecimal getCuota() { return cuota; }
        public BigDecimal getInteres() { return interes; }
        public BigDecimal getCapital() { return capital; }
        public BigDecimal getSaldoFinal() { return saldoFinal; }
    }

    private Long idSolicitud;
    private BigDecimal montoSolicitado;
    private BigDecimal tasaInteresEA;
    private BigDecimal tasaInteresMensual;
    private int plazoMeses;
    private List<CuotaResponse> cuotas;

    public AmortizacionResponse(Long idSolicitud, BigDecimal montoSolicitado, BigDecimal tasaInteresEA,
                                 BigDecimal tasaInteresMensual, int plazoMeses, List<CuotaResponse> cuotas) {
        this.idSolicitud = idSolicitud;
        this.montoSolicitado = montoSolicitado;
        this.tasaInteresEA = tasaInteresEA;
        this.tasaInteresMensual = tasaInteresMensual;
        this.plazoMeses = plazoMeses;
        this.cuotas = cuotas;
    }

    public Long getIdSolicitud() { return idSolicitud; }
    public BigDecimal getMontoSolicitado() { return montoSolicitado; }
    public BigDecimal getTasaInteresEA() { return tasaInteresEA; }
    public BigDecimal getTasaInteresMensual() { return tasaInteresMensual; }
    public int getPlazoMeses() { return plazoMeses; }
    public List<CuotaResponse> getCuotas() { return cuotas; }
}
