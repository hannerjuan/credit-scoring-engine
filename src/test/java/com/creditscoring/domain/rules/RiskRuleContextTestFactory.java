package com.creditscoring.domain.rules;

import com.creditscoring.domain.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Construye RiskRuleContext de prueba con valores por defecto razonables, sobrescribibles por caso. */
public final class RiskRuleContextTestFactory {

    private RiskRuleContextTestFactory() { }

    public static Cliente clienteDefault() {
        return new Cliente(1L, "123456789", "Juan Perez",
                LocalDate.now().minusYears(30), TipoContrato.INDEFINIDO, 24, "Comercio");
    }

    public static SolicitudCredito solicitudDefault(Cliente cliente) {
        return new SolicitudCredito(1L, cliente, BigDecimal.valueOf(10_000_000), 24,
                BigDecimal.valueOf(3_000_000), BigDecimal.valueOf(500_000), EstadoSolicitud.PENDIENTE, null);
    }

    public static BuroCreditoResultado buroDefault() {
        return new BuroCreditoResultado(CalificacionBuro.A, 800, 0, LocalDateTime.now());
    }

    public static RiskRuleContext contextoConDti(BigDecimal dti) {
        Cliente cliente = clienteDefault();
        return new RiskRuleContext(cliente, solicitudDefault(cliente), buroDefault(),
                BigDecimal.valueOf(500_000), dti, BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(600_000), 30);
    }

    public static RiskRuleContext contextoConBuro(BuroCreditoResultado buro) {
        Cliente cliente = clienteDefault();
        return new RiskRuleContext(cliente, solicitudDefault(cliente), buro,
                BigDecimal.valueOf(500_000), BigDecimal.valueOf(20), BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(600_000), 30);
    }

    public static RiskRuleContext contextoConEdad(int edad) {
        Cliente cliente = clienteDefault();
        return new RiskRuleContext(cliente, solicitudDefault(cliente), buroDefault(),
                BigDecimal.valueOf(500_000), BigDecimal.valueOf(20), BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(600_000), edad);
    }

    public static RiskRuleContext contextoConCapacidadResidual(BigDecimal capacidadResidual, BigDecimal umbral) {
        Cliente cliente = clienteDefault();
        return new RiskRuleContext(cliente, solicitudDefault(cliente), buroDefault(),
                BigDecimal.valueOf(500_000), BigDecimal.valueOf(20), capacidadResidual, umbral, 30);
    }

    public static RiskRuleContext contextoConTipoContratoYAntiguedad(TipoContrato tipoContrato, int antiguedadMeses) {
        Cliente cliente = new Cliente(1L, "123456789", "Juan Perez",
                LocalDate.now().minusYears(30), tipoContrato, antiguedadMeses, "Comercio");
        return new RiskRuleContext(cliente, solicitudDefault(cliente), buroDefault(),
                BigDecimal.valueOf(500_000), BigDecimal.valueOf(20), BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(600_000), 30);
    }

    public static RiskRuleContext contextoConIngresos(BigDecimal ingresosMensuales) {
        Cliente cliente = clienteDefault();
        SolicitudCredito solicitud = new SolicitudCredito(1L, cliente, BigDecimal.valueOf(10_000_000), 24,
                ingresosMensuales, BigDecimal.valueOf(500_000), EstadoSolicitud.PENDIENTE, null);
        return new RiskRuleContext(cliente, solicitud, buroDefault(),
                BigDecimal.valueOf(500_000), BigDecimal.valueOf(20), BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(600_000), 30);
    }
}
