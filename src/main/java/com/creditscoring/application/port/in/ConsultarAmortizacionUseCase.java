package com.creditscoring.application.port.in;

import com.creditscoring.application.dto.AmortizacionResponse;

public interface ConsultarAmortizacionUseCase {

    AmortizacionResponse consultar(Long idSolicitud);
}
