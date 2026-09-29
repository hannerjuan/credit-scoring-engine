package com.creditscoring.domain.rules;

import com.creditscoring.domain.model.BuroCreditoResultado;
import com.creditscoring.domain.model.Cliente;
import com.creditscoring.domain.model.SolicitudCredito;

import java.math.BigDecimal;

/**
 * Contiene todos los datos ya pre-calculados que una RiskRule pueda
 * necesitar para emitir su veredicto. Cada regla lee de aqui solo lo que
 * le interesa, sin acoplarse a como se calculo el resto.
 */
public final class RiskRuleContext {

    private final Cliente cliente;
    private final SolicitudCredito solicitud;
    private final BuroCreditoResultado buro;
    private final BigDecimal cuotaProyectada;
    private final BigDecimal dtiCalculado;
    private final BigDecimal capacidadPagoResidual;
    private final BigDecimal umbralSubsistencia;
    private final int edadSolicitante;

    public RiskRuleContext(Cliente cliente, SolicitudCredito solicitud, BuroCreditoResultado buro,
                            BigDecimal cuotaProyectada, BigDecimal dtiCalculado,
                            BigDecimal capacidadPagoResidual, BigDecimal umbralSubsistencia,
                            int edadSolicitante) {
        this.cliente = cliente;
        this.solicitud = solicitud;
        this.buro = buro;
        this.cuotaProyectada = cuotaProyectada;
        this.dtiCalculado = dtiCalculado;
        this.capacidadPagoResidual = capacidadPagoResidual;
        this.umbralSubsistencia = umbralSubsistencia;
        this.edadSolicitante = edadSolicitante;
    }

    public Cliente getCliente() { return cliente; }
    public SolicitudCredito getSolicitud() { return solicitud; }
    public BuroCreditoResultado getBuro() { return buro; }
    public BigDecimal getCuotaProyectada() { return cuotaProyectada; }
    public BigDecimal getDtiCalculado() { return dtiCalculado; }
    public BigDecimal getCapacidadPagoResidual() { return capacidadPagoResidual; }
    public BigDecimal getUmbralSubsistencia() { return umbralSubsistencia; }
    public int getEdadSolicitante() { return edadSolicitante; }
}
