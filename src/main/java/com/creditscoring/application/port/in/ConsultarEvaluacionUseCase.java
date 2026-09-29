package com.creditscoring.application.port.in;

import com.creditscoring.application.dto.EvaluacionResponse;

public interface ConsultarEvaluacionUseCase {

    EvaluacionResponse consultar(Long idSolicitud);
}
