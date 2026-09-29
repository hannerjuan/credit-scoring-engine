package com.creditscoring.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CapacidadPagoCalculatorTest {

    private final CapacidadPagoCalculator calculator = new CapacidadPagoCalculator();

    @ParameterizedTest(name = "ingresos={0}, egresos={1}, cuota={2} => residual={3}")
    @DisplayName("Capacidad residual = ingresos - egresos - cuota proyectada")
    @CsvSource({
            "2000000, 500000, 800000, 700000",
            "1000000, 400000, 600000, 0",        // borde exacto: residual cero
            "1000000, 500000, 600000, -100000",  // negativo: por debajo de subsistencia
            "3000000, 0, 0, 3000000"
    })
    void calculaCapacidadResidual(String ingresos, String egresos, String cuota, String residualEsperado) {
        BigDecimal residual = calculator.calcularCapacidadResidual(
                new BigDecimal(ingresos), new BigDecimal(egresos), new BigDecimal(cuota));
        assertThat(residual).isEqualByComparingTo(new BigDecimal(residualEsperado));
    }

    @ParameterizedTest(name = "residual={0}, umbral={1} => superaUmbral={2}")
    @DisplayName("Verifica el borde exacto del umbral de subsistencia (>=  es suficiente)")
    @CsvSource({
            "600000, 600000, true",   // exactamente en el umbral: SI alcanza
            "599999, 600000, false",  // un peso por debajo: NO alcanza
            "600001, 600000, true",
            "0, 600000, false"
    })
    void verificaUmbralSubsistencia(String residual, String umbral, boolean esperado) {
        boolean resultado = calculator.superaUmbralSubsistencia(new BigDecimal(residual), new BigDecimal(umbral));
        assertThat(resultado).isEqualTo(esperado);
    }
}
