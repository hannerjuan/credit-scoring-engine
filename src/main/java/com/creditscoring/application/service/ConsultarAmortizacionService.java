package com.creditscoring.application.service;

import com.creditscoring.application.dto.AmortizacionResponse;
import com.creditscoring.application.port.in.ConsultarAmortizacionUseCase;
import com.creditscoring.application.port.out.ResultadoEvaluacionRepositoryPort;
import com.creditscoring.application.port.out.SolicitudRepositoryPort;
import com.creditscoring.domain.exception.AmortizacionNoDisponibleException;
import com.creditscoring.domain.exception.EvaluacionNoDisponibleException;
import com.creditscoring.domain.exception.SolicitudNotFoundException;
import com.creditscoring.domain.model.CuotaAmortizacion;
import com.creditscoring.domain.model.Dictamen;
import com.creditscoring.domain.model.ResultadoEvaluacion;
import com.creditscoring.domain.model.SolicitudCredito;
import com.creditscoring.domain.model.TablaAmortizacion;
import com.creditscoring.domain.service.AmortizacionFrancesaCalculator;

import java.util.List;

/**
 * La tabla de amortizacion no se persiste como entidad propia (ver modelo de
 * datos): se recalcula de forma determinista a partir del monto/plazo de la
 * solicitud y de la tasa preferencial que quedo registrada en el resultado
 * de evaluacion. Solo esta disponible para solicitudes con dictamen APROBADO.
 */
public class ConsultarAmortizacionService implements ConsultarAmortizacionUseCase {

    private final SolicitudRepositoryPort solicitudRepository;
    private final ResultadoEvaluacionRepositoryPort resultadoRepository;
    private final AmortizacionFrancesaCalculator amortizacionCalculator;

    public ConsultarAmortizacionService(SolicitudRepositoryPort solicitudRepository,
                                         ResultadoEvaluacionRepositoryPort resultadoRepository,
                                         AmortizacionFrancesaCalculator amortizacionCalculator) {
        this.solicitudRepository = solicitudRepository;
        this.resultadoRepository = resultadoRepository;
        this.amortizacionCalculator = amortizacionCalculator;
    }

    @Override
    public AmortizacionResponse consultar(Long idSolicitud) {
        SolicitudCredito solicitud = solicitudRepository.buscarPorId(idSolicitud)
                .orElseThrow(() -> new SolicitudNotFoundException(idSolicitud));

        ResultadoEvaluacion resultado = resultadoRepository.buscarPorIdSolicitud(idSolicitud)
                .orElseThrow(() -> new EvaluacionNoDisponibleException(idSolicitud));

        if (resultado.getDictamen() != Dictamen.APROBADO) {
            throw new AmortizacionNoDisponibleException(idSolicitud, resultado.getDictamen().name());
        }

        TablaAmortizacion tabla = amortizacionCalculator.generarTabla(
                idSolicitud, solicitud.getMontoSolicitado(), resultado.getTasaInteresEA(), solicitud.getPlazoMeses());

        List<AmortizacionResponse.CuotaResponse> cuotas = tabla.getCuotas().stream()
                .map(this::toCuotaResponse)
                .toList();

        return new AmortizacionResponse(
                tabla.getIdSolicitud(),
                tabla.getMontoSolicitado(),
                tabla.getTasaInteresEA(),
                tabla.getTasaInteresMensual(),
                tabla.getPlazoMeses(),
                cuotas);
    }

    private AmortizacionResponse.CuotaResponse toCuotaResponse(CuotaAmortizacion c) {
        return new AmortizacionResponse.CuotaResponse(
                c.getNumeroCuota(), c.getSaldoInicial(), c.getCuota(), c.getInteres(), c.getCapital(), c.getSaldoFinal());
    }
}
