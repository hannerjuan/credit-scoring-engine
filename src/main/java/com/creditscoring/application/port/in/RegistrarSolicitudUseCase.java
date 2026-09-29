package com.creditscoring.application.port.in;

import com.creditscoring.application.dto.SolicitudCreditoRequest;
import com.creditscoring.application.dto.SolicitudCreditoResponse;

public interface RegistrarSolicitudUseCase {

    SolicitudCreditoResponse registrar(SolicitudCreditoRequest request);
}
