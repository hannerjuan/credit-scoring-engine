package com.creditscoring.application.service;

import com.creditscoring.application.dto.EvaluacionResponse;
import com.creditscoring.application.port.in.ConsultarEvaluacionUseCase;
import com.creditscoring.application.port.out.ResultadoEvaluacionRepositoryPort;
import com.creditscoring.application.port.out.SolicitudRepositoryPort;
import com.creditscoring.domain.exception.EvaluacionNoDisponibleException;
import com.creditscoring.domain.exception.SolicitudNotFoundException;
import com.creditscoring.domain.model.ResultadoEvaluacion;

public class ConsultarEvaluacionService implements ConsultarEvaluacionUseCase {

    private final SolicitudRepositoryPort solicitudRepository;
    private final ResultadoEvaluacionRepositoryPort resultadoRepository;

    public ConsultarEvaluacionService(SolicitudRepositoryPort solicitudRepository,
                                       ResultadoEvaluacionRepositoryPort resultadoRepository) {
        this.solicitudRepository = solicitudRepository;
        this.resultadoRepository = resultadoRepository;
    }

    @Override
    public EvaluacionResponse consultar(Long idSolicitud) {
        solicitudRepository.buscarPorId(idSolicitud)
                .orElseThrow(() -> new SolicitudNotFoundException(idSolicitud));

        ResultadoEvaluacion resultado = resultadoRepository.buscarPorIdSolicitud(idSolicitud)
                .orElseThrow(() -> new EvaluacionNoDisponibleException(idSolicitud));

        return EvaluarSolicitudService.toResponse(resultado);
    }
}
