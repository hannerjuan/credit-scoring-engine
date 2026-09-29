package com.creditscoring.domain.service;

import com.creditscoring.domain.model.CuotaAmortizacion;
import com.creditscoring.domain.model.TablaAmortizacion;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO puro de dominio. Calcula la cuota fija mensual y la tabla de
 * amortizacion completa bajo el sistema frances:
 *
 *   Cuota = C * i * (1+i)^n / ((1+i)^n - 1)
 *
 * Sin dependencias de Spring, JPA ni ningun otro framework.
 */
public class AmortizacionFrancesaCalculator {

    private static final int ESCALA_MONETARIA = 2;
    private static final int PRECISION_INTERNA = 20;
    private static final MathContext MC = new MathContext(PRECISION_INTERNA);

    /**
     * Calcula la cuota fija mensual.
     *
     * @param montoSolicitado capital C
     * @param tasaMensual     tasa periodica mensual i, expresada en decimal (ej: 0.015 = 1.5%)
     * @param plazoMeses      numero de cuotas n
     */
    public BigDecimal calcularCuotaMensual(BigDecimal montoSolicitado, BigDecimal tasaMensual, int plazoMeses) {
        if (plazoMeses <= 0) {
            throw new IllegalArgumentException("plazoMeses debe ser mayor a cero");
        }
        if (montoSolicitado.signum() <= 0) {
            throw new IllegalArgumentException("montoSolicitado debe ser mayor a cero");
        }

        // Tasa 0% -> cuota simple sin interes (caso borde defensivo, evita division por cero).
        if (tasaMensual.signum() == 0) {
            return montoSolicitado.divide(BigDecimal.valueOf(plazoMeses), ESCALA_MONETARIA, RoundingMode.HALF_UP);
        }

        BigDecimal unoMasI = BigDecimal.ONE.add(tasaMensual, MC);
        BigDecimal factor = unoMasI.pow(plazoMeses, MC);
        BigDecimal numerador = montoSolicitado.multiply(tasaMensual, MC).multiply(factor, MC);
        BigDecimal denominador = factor.subtract(BigDecimal.ONE, MC);

        return numerador.divide(denominador, ESCALA_MONETARIA, RoundingMode.HALF_UP);
    }

    /** Convierte una tasa efectiva anual (EA) a su equivalente tasa periodica mensual. */
    public BigDecimal convertirEaAMensual(BigDecimal tasaEA) {
        // i_mensual = (1 + i_EA)^(1/12) - 1
        BigDecimal unoMasEA = BigDecimal.ONE.add(tasaEA, MC);
        double raiz = Math.pow(unoMasEA.doubleValue(), 1.0 / 12.0);
        return BigDecimal.valueOf(raiz).subtract(BigDecimal.ONE, MC).round(new MathContext(10));
    }

    /**
     * Genera la tabla de amortizacion completa (cuota fija, desglose de
     * capital, intereses y saldo residual mes a mes).
     */
    public TablaAmortizacion generarTabla(Long idSolicitud, BigDecimal montoSolicitado,
                                           BigDecimal tasaInteresEA, int plazoMeses) {
        BigDecimal tasaMensual = convertirEaAMensual(tasaInteresEA);
        BigDecimal cuota = calcularCuotaMensual(montoSolicitado, tasaMensual, plazoMeses);

        List<CuotaAmortizacion> filas = new ArrayList<>(plazoMeses);
        BigDecimal saldo = montoSolicitado.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

        for (int mes = 1; mes <= plazoMeses; mes++) {
            BigDecimal saldoInicial = saldo;
            BigDecimal interesMes = saldoInicial.multiply(tasaMensual, MC).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);

            BigDecimal cuotaMes = cuota;
            BigDecimal capitalMes;
            BigDecimal saldoFinal;

            if (mes == plazoMeses) {
                // Ultima cuota: se ajusta para saldar exactamente el saldo pendiente
                // y absorber los residuos de redondeo acumulados.
                capitalMes = saldoInicial;
                cuotaMes = capitalMes.add(interesMes);
                saldoFinal = BigDecimal.ZERO.setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
            } else {
                capitalMes = cuotaMes.subtract(interesMes).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
                saldoFinal = saldoInicial.subtract(capitalMes).setScale(ESCALA_MONETARIA, RoundingMode.HALF_UP);
            }

            filas.add(new CuotaAmortizacion(mes, saldoInicial, cuotaMes, interesMes, capitalMes, saldoFinal));
            saldo = saldoFinal;
        }

        return new TablaAmortizacion(idSolicitud, montoSolicitado, tasaInteresEA, tasaMensual, plazoMeses, filas);
    }
}
