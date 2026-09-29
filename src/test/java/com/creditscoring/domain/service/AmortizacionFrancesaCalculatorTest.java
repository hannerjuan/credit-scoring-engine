package com.creditscoring.domain.service;

import com.creditscoring.domain.model.TablaAmortizacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmortizacionFrancesaCalculatorTest {

    private final AmortizacionFrancesaCalculator calculator = new AmortizacionFrancesaCalculator();

    @ParameterizedTest(name = "monto={0}, tasaMensual={1}, plazo={2} => cuota~={3}")
    @DisplayName("Cuota = C * i * (1+i)^n / ((1+i)^n - 1), validado contra valores de referencia")
    @CsvSource({
            // Valores de referencia calculados de forma independiente (formula estandar sistema frances).
            "1000000, 0.02, 12, 94559.99",
            "10000000, 0.015, 36, 361548.28",
            "5000000, 0.01, 24, 235366.55"
    })
    void calculaCuotaMensualCorrectamente(String monto, String tasaMensual, int plazo, String cuotaEsperada) {
        BigDecimal cuota = calculator.calcularCuotaMensual(new BigDecimal(monto), new BigDecimal(tasaMensual), plazo);
        assertThat(cuota).isCloseTo(new BigDecimal(cuotaEsperada), org.assertj.core.data.Percentage.withPercentage(0.01));
    }

    @Test
    @DisplayName("Con tasa 0% la cuota es simplemente el capital dividido en partes iguales")
    void tasaCeroDivideCapitalEnPartesIguales() {
        BigDecimal cuota = calculator.calcularCuotaMensual(BigDecimal.valueOf(1_200_000), BigDecimal.ZERO, 12);
        assertThat(cuota).isEqualByComparingTo(BigDecimal.valueOf(100_000).setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    @DisplayName("Rechaza plazo <= 0 y monto <= 0")
    void validaParametrosDeEntrada() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calcularCuotaMensual(BigDecimal.valueOf(1000), BigDecimal.valueOf(0.01), 0));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calcularCuotaMensual(BigDecimal.ZERO, BigDecimal.valueOf(0.01), 12));
    }

    @Test
    @DisplayName("La tabla de amortizacion generada salda el credito exactamente en la ultima cuota")
    void tablaDeAmortizacionSaldaElCreditoCompletamente() {
        TablaAmortizacion tabla = calculator.generarTabla(1L, BigDecimal.valueOf(10_000_000), BigDecimal.valueOf(0.18), 24);

        assertThat(tabla.getCuotas()).hasSize(24);
        // El saldo final de la ultima cuota debe ser exactamente cero.
        assertThat(tabla.getCuotas().get(23).getSaldoFinal()).isEqualByComparingTo(BigDecimal.ZERO);

        // La suma de capital pagado en todas las cuotas debe ser igual al monto solicitado.
        BigDecimal totalCapitalPagado = tabla.getCuotas().stream()
                .map(com.creditscoring.domain.model.CuotaAmortizacion::getCapital)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertThat(totalCapitalPagado).isEqualByComparingTo(BigDecimal.valueOf(10_000_000).setScale(2));

        // El saldo inicial de cada cuota debe coincidir con el saldo final de la anterior.
        for (int i = 1; i < tabla.getCuotas().size(); i++) {
            assertThat(tabla.getCuotas().get(i).getSaldoInicial())
                    .isEqualByComparingTo(tabla.getCuotas().get(i - 1).getSaldoFinal());
        }
    }

    @Test
    @DisplayName("Convierte correctamente una tasa EA a su equivalente mensual")
    void convierteEaAMensual() {
        // 12% EA equivale aproximadamente a 0.9489% mensual.
        BigDecimal mensual = calculator.convertirEaAMensual(BigDecimal.valueOf(0.12));
        assertThat(mensual.doubleValue()).isCloseTo(0.009489, org.assertj.core.data.Offset.offset(0.0001));
    }
}
