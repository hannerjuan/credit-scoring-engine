package com.creditscoring.application.port.in;

import com.creditscoring.application.dto.EvaluacionResponse;

public interface EvaluarSolicitudUseCase {

    EvaluacionResponse evaluar(Long idSolicitud);
}
