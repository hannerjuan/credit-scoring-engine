package com.creditscoring.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DtiCalculatorTest {

    private final DtiCalculator calculator = new DtiCalculator();

    @ParameterizedTest(name = "ingresos={0}, egresos={1}, cuota={2} => dti={3}%")
    @DisplayName("Calcula el DTI = (egresos + cuota) / ingresos * 100 para varias combinaciones, incluyendo bordes")
    @CsvSource({
            // ingresos, egresos, cuotaProyectada, dtiEsperado
            "1000000, 0, 0, 0.00",
            "1000000, 200000, 150000, 35.00",   // borde exacto del umbral optimo
            "1000000, 250000, 200000, 45.00",   // borde exacto del umbral maximo
            "2000000, 500000, 400000, 45.00",
            "1000000, 300000, 250000, 55.00",   // supera el maximo
            "3000000, 100000, 50000, 5.00"
    })
    void calculaDtiCorrectamente(String ingresos, String egresos, String cuota, String dtiEsperado) {
        BigDecimal resultado = calculator.calcular(new BigDecimal(ingresos), new BigDecimal(egresos), new BigDecimal(cuota));
        assertThat(resultado).isEqualByComparingTo(new BigDecimal(dtiEsperado));
    }

    @Test
    @DisplayName("Lanza excepcion si los ingresos mensuales son cero o negativos")
    void lanzaExcepcionSiIngresosNoSonPositivos() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calcular(BigDecimal.ZERO, BigDecimal.valueOf(100), BigDecimal.valueOf(50)));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calcular(BigDecimal.valueOf(-1), BigDecimal.valueOf(100), BigDecimal.valueOf(50)));
    }
}
