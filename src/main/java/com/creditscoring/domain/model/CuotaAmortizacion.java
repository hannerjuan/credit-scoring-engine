package com.creditscoring.domain.model;

import java.math.BigDecimal;

/** Una fila de la tabla de amortizacion bajo sistema frances (cuota fija). */
public final class CuotaAmortizacion {

    private final int numeroCuota;
    private final BigDecimal saldoInicial;
    private final BigDecimal cuota;
    private final BigDecimal interes;
    private final BigDecimal capital;
    private final BigDecimal saldoFinal;

    public CuotaAmortizacion(int numeroCuota, BigDecimal saldoInicial, BigDecimal cuota,
                              BigDecimal interes, BigDecimal capital, BigDecimal saldoFinal) {
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
